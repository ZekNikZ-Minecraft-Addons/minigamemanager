package io.zkz.mc.minigamemanager.scoreboard.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.scoreboard.GameScoreboard
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ComponentEntry
import io.zkz.mc.gametools.scoreboard.entry.TimerEntry
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.scoreboard.TeamScoresScoreboardEntry
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.MinigameState
import io.zkz.mc.minigamemanager.state.impl.ReadyUpMinigameState
import org.bukkit.entity.Player

object StandardMinigameScoreboard : CachedMinigameScoreboard(), InjectionComponent {
    private val minigameService by inject<MinigameService>()
    private val scoreboardService by inject<ScoreboardService>()

    const val KEY_READY_PLAYER_COUNT = "readyPlayerCount"

    override fun buildScoreboard(team: GameTeam?, state: MinigameState): GameScoreboard {
        val scoreboard: GameScoreboard = scoreboardService
            .createNewScoreboard(mm("<legacy_gold><bold>${minigameService.config.tournamentName}"))

        // Game name
        if (minigameService.config.currentGameNumber != null) {
            if (minigameService.config.maxGameNumber != null) {
                scoreboard.addEntry(
                    "gameName",
                    ComponentEntry(
                        mm(
                            "<legacy_aqua><bold>Game ${
                            minigameService.config.currentGameNumber
                            }/${
                            minigameService.config.maxGameNumber
                            }:</bold></legacy_aqua> ${
                            minigameService.config.minigameName
                            }",
                        ),
                    ),
                )
            } else {
                scoreboard.addEntry(
                    "gameName",
                    ComponentEntry(
                        mm(
                            "<legacy_aqua><bold>Game ${
                            minigameService.config.currentGameNumber
                            }:</bold></legacy_aqua> ${
                            minigameService.config.minigameName
                            }",
                        ),
                    ),
                )
            }
        }

        // Extra info
        addRoundAndStateInfo(scoreboard, state)

        // State-specific stuff
        if (state is ReadyUpMinigameState) {
            scoreboard.addSpace()
            scoreboard.addEntry(
                KEY_READY_PLAYER_COUNT,
                ValueEntry(
                    "<legacy_green><bold>Ready players:</bold></legacy_green> <value>/${
                    minigameService.participantsAndGameMasters.size
                    }",
                    0,
                ),
            )
        }
        if (state != DefaultStates.SERVER_STARTING && state != DefaultStates.SETUP && state != DefaultStates.WAITING_FOR_PLAYERS && state != DefaultStates.RULES && state != DefaultStates.WAITING_TO_BEGIN) {
            addGlobalMinigameTimer(scoreboard)
            addTeamScores(scoreboard, team)
        }

        return scoreboard
    }

    override fun buildScoreboard(player: Player, teamOfPlayer: GameTeam?, state: MinigameState) = null

    fun addRoundAndStateInfo(scoreboard: GameScoreboard, state: MinigameState) {
        if (minigameService.currentRoundIndex >= 0 || state == DefaultStates.WAITING_FOR_PLAYERS) {
            // Map info
            minigameService.currentRound.mapName?.let {
                scoreboard.addEntry(mm("<legacy_aqua><bold>Map:</bold></legacy_aqua> $it"))
                minigameService.currentRound.mapBy?.let {
                    scoreboard.addEntry(mm("<legacy_aqua><bold>Map by:</bold></legacy_aqua> $it"))
                }
            }

            // Round info
            if (minigameService.numRounds > 1) {
                scoreboard.addEntry(mm("<legacy_green><bold>Round:</bold></legacy_green> ${minigameService.currentRoundIndex + 1} / ${minigameService.numRounds}"))
            }
        }

        // Game status
        state.gameStatusString?.let {
            scoreboard.addSpace()
            scoreboard.addEntry(mm("<legacy_red><bold>Game status:"))
            scoreboard.addEntry(mm(it))
        }
    }

    fun addGlobalMinigameTimer(scoreboard: GameScoreboard) {
        if (minigameService.globalTimer != null) {
            scoreboard.addEntry(
                TimerEntry(
                    "<legacy_red><bold><label></bold></legacy_red> <value>",
                    minigameService.globalTimerLabel ?: mm("Timer:"),
                    minigameService.globalTimer!!,
                ),
            )
        } else if (minigameService.globalTimerLabel != null) {
            scoreboard.addEntry(
                mm(
                    "<legacy_red><bold><0></bold></legacy_red> waiting...",
                    minigameService.globalTimerLabel,
                ),
            )
        }
    }

    fun addTeamScores(scoreboard: GameScoreboard, team: GameTeam?) {
        scoreboard.addSpace()
        scoreboard.addEntry("teamScores", TeamScoresScoreboardEntry(team))
    }

    fun addPlayerScores(scoreboard: GameScoreboard, player: Player) {
        // TODO
    }
}
