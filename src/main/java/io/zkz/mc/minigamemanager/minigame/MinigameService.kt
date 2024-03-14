package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.team.DefaultTeams
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.gametools.timer.AbstractTimer
import io.zkz.mc.gametools.timer.GameCountdownTimer
import io.zkz.mc.gametools.util.BukkitUtils.forEachPlayer
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.gametools.util.PlayerUtils.allOnline
import io.zkz.mc.gametools.util.PlayerUtils.filterOnline
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.MinigameManagerPlugin
import io.zkz.mc.minigamemanager.event.StateChangeEvent
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.MinigameState
import io.zkz.mc.minigamemanager.task.MinigameTask
import io.zkz.mc.minigamemanager.task.RulesTask
import io.zkz.mc.minigamemanager.task.ScoreSummaryTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import kotlin.time.DurationUnit

@Injectable
class MinigameService(
    plugin: MinigameManagerPlugin,
    private val teamService: TeamService,
    private val scoreboardService: ScoreboardService,
) : PluginService<MinigameManagerPlugin>(plugin) {
    private val states: MutableMap<String, MinigameState> = mutableMapOf()
    var currentState: MinigameState = DefaultStates.SERVER_STARTING
        private set
    var config: MinigameConfig = MinigameConfig.DEFAULT_CONFIG

    fun <T : MinigameState> registerState(state: T, replace: Boolean = false): T {
        if (!replace && states.containsKey(state.id)) {
            throw IllegalStateException("State with ID ${state.id} already exists")
        }

        states[state.id] = state
        state.init()
        return state
    }

    fun <T : MinigameState> registerState(stateBuilder: () -> T): T {
        return stateBuilder().also { registerState(it) }
    }

    fun setState(state: MinigameState) {
        logger.info("Attempting state transition: ${currentState.id} => ${state.id}")
        val oldState = currentState
        val newState = states[state.id] ?: throw IllegalStateException("State with id '${state.id}' is not registered")
        val preEvent = StateChangeEvent.Pre(oldState, newState)
        event(preEvent)

        if (preEvent.isCancelled) {
            logger.info("Attempting state transition cancelled.")
            return
        }

        runningTasks.forEach { it.cancel(false) }
        runningTasks.clear()

        currentState.onExit()
        currentState = newState
        newState.onEnter()

        event(StateChangeEvent.Post(oldState, newState))

        scoreboardService.resetAllScoreboards()
        refreshScoreboards()
    }

    override fun onEnable() {
        DefaultStates.init()

        // Setup WAITING_FOR_PLAYERS
        DefaultStates.WAITING_FOR_PLAYERS.handleEnter {
            addTask(1, 20, ::waitForPlayers)
        }

        // Setup RULES
        DefaultStates.RULES.handleEnter {
            addTask(RulesTask())
        }

        // Setup POST_GAME
        DefaultStates.POST_GAME.handleEnter {
            setGlobalTimer(
                GameCountdownTimer(
                    plugin,
                    20,
                    config.postGameDelayInTicks * 50L + ScoreSummaryTask.SECONDS_PER_SLIDE * ScoreSummaryTask.NUM_SLIDES * 20L,
                    DurationUnit.MILLISECONDS,
                ) {
                    setState(DefaultStates.GAME_OVER)
                },
                mm("Back to hub in:"),
            )

            addTask(ScoreSummaryTask())
        }

        // Transition to next state
        runNextTick {
            // Make sure this happens last
            DefaultStates.SETUP.handleEnter {
                setState(DefaultStates.WAITING_FOR_PLAYERS)
            }

            setState(DefaultStates.SETUP)
        }
    }

    val participants: Collection<UUID>
        get() = teamService.trackedPlayers
            .plus(Bukkit.getOnlinePlayers().map { it.uniqueId })
            .distinct()
            .filter {
                val team: GameTeam? = teamService.getTeamOfPlayer(it)
                return@filter if (this.config.isTeamGame) {
                    team != null && !team.isSpectator
                } else {
                    team == null || !team.isSpectator
                }
            }

    val participantsAndGameMasters: Collection<UUID>
        get() = teamService.trackedPlayers
            .plus(Bukkit.getOnlinePlayers().map { it.uniqueId })
            .distinct()
            .filter {
                val team: GameTeam? = teamService.getTeamOfPlayer(it)
                return@filter if (this.config.isTeamGame) {
                    team != null && (team == DefaultTeams.GAME_MASTER || !team.isSpectator)
                } else {
                    team == null || team == DefaultTeams.GAME_MASTER || !team.isSpectator
                }
            }

    private val runningTasks: MutableSet<MinigameTask> = mutableSetOf()
    internal fun registerTask(task: MinigameTask) {
        runningTasks.add(task)
    }

    internal fun removeRunningTask(task: MinigameTask) {
        runningTasks.remove(task)
    }

    var globalTimer: AbstractTimer? = null
        private set
    var globalTimerLabel: Component? = null
        private set

    fun setGlobalTimer(timer: AbstractTimer? = null, label: Component? = null) {
        timer?.stop()

        globalTimer = timer
        globalTimerLabel = label

        globalTimer?.start()

        refreshScoreboards()
    }

    fun refreshScoreboards() {
        var state = currentState
        var scoreboard = state.buildScoreboard()
        while (scoreboard == null) {
            state = state.parentState ?: break
            scoreboard = state.buildScoreboard()
        }
        if (scoreboard == null) {
            return
        }
        scoreboard.init(currentState)
        forEachPlayer { scoreboard.apply(it, teamService.getTeamOfPlayer(it)) }
        scoreboard.cleanup()
    }

    private fun waitForPlayers() {
        // Update scoreboards
        val numReadyParticipants = participants.filterOnline().count()
        scoreboardService.allScoreboards.forEach { scoreboard ->
            val entry = scoreboard.getEntry<ValueEntry<Int>>("playerCount")
            entry?.value = numReadyParticipants
        }

        // Check if we should move on
        if (config.shouldAutomaticallyShowRules && participants.allOnline()) {
            setState(DefaultStates.RULES)
        }
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        refreshScoreboards()
    }
}
