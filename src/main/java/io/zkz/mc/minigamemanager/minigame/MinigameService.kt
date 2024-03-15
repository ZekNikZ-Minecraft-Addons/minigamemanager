package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.team.DefaultTeams
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.gametools.timer.AbstractTimer
import io.zkz.mc.gametools.util.BukkitUtils.forEachPlayer
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.minigamemanager.MinigameManagerPlugin
import io.zkz.mc.minigamemanager.event.RoundChangeEvent
import io.zkz.mc.minigamemanager.event.StateChangeEvent
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.MinigameState
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import io.zkz.mc.minigamemanager.task.MinigameTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*

@Injectable
class MinigameService(
    plugin: MinigameManagerPlugin,
    private val teamService: TeamService,
    private val scoreboardService: ScoreboardService,
) : PluginService<MinigameManagerPlugin>(plugin) {
    private val states = mutableMapOf<String, MinigameState>()
    private val rounds = mutableListOf<Round>()
    var currentRoundIndex = -1
        private set
    val currentRound
        get() = rounds[currentRoundIndex]
    val numRounds
        get() = rounds.size
    val isLastRound
        get() = currentRoundIndex == rounds.size - 1

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

    fun registerRound(vararg rounds: Round) {
        registerRounds(listOf(*rounds))
    }

    fun registerRounds(rounds: List<Round>) {
        this.rounds.addAll(rounds)
    }

    fun setState(state: MinigameState) {
        logger.info("Attempting state transition: ${currentState.id} => ${state.id}")
        val oldState = currentState
        val newState = states[state.id] ?: throw IllegalStateException("State with id '${state.id}' is not registered")
        val preEvent = StateChangeEvent.Pre(oldState, newState)
        event(preEvent)

        if (preEvent.isCancelled) {
            logger.info("State transition cancelled.")
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

    fun setRound(roundIndex: Int) {
        if (roundIndex < 0 || roundIndex >= rounds.size) {
            throw IllegalArgumentException("Round index $roundIndex out of bounds")
        }

        logger.info("Attempting round transition: $currentRoundIndex => $roundIndex")
        val oldRound = rounds[currentRoundIndex]
        val newRound = rounds[roundIndex]
        val preEvent = RoundChangeEvent.Pre(oldRound, newRound)
        event(preEvent)

        if (preEvent.isCancelled) {
            logger.info("Round transition cancelled.")
            return
        }

        if (currentRoundIndex >= 0) {
            currentRound.onDeselected()
        }
        currentRoundIndex = roundIndex
        currentRound.onSelected()

        event(RoundChangeEvent.Post(oldRound, newRound))

        refreshScoreboards()
    }

    inline fun <reified T : Round> getCurrentRound(): T {
        return currentRound as? T ?: throw IllegalStateException("Round is not of type ${T::class.simpleName}")
    }

    override fun onEnable() {
        DefaultStates.init()

        // Transition to next state
        runNextTick {
            setState(DefaultStates.SETUP)

            setupRoundHandlers()
        }
    }

    private fun setupRoundHandlers() {
        states.forEach {
            if (it is DelegatedMinigameState) {
                it.handleEnter {
                    currentRound.onEnterState(this as DelegatedMinigameState)
                }
            }
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
        val scoreboard = currentState.buildScoreboard() ?: return
        scoreboard.init(currentState)
        forEachPlayer { scoreboard.apply(it, teamService.getTeamOfPlayer(it)) }
        scoreboard.cleanup()
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        refreshScoreboards()
    }
}
