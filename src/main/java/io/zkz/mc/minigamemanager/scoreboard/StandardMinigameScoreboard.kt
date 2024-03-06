package io.zkz.mc.minigamemanager.scoreboard

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.scoreboard.GameScoreboard
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ComponentEntry
import io.zkz.mc.gametools.scoreboard.entry.TimerEntry
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.IHasMapInfo
import io.zkz.mc.minigamemanager.state.IHasRoundInfo
import io.zkz.mc.minigamemanager.state.IHasStateInfo
import io.zkz.mc.minigamemanager.state.MinigameState
import io.zkz.mc.minigamemanager.state.impl.ReadyUpMinigameState
import org.bukkit.entity.Player

object StandardMinigameScoreboard : MinigameScoreboard, InjectionComponent {
    private val minigameService by inject<MinigameService>()
    private val teamService by inject<TeamService>()
    private val scoreboardService by inject<ScoreboardService>()

    const val KEY_READY_PLAYER_COUNT = "readyPlayerCount"

    private val teamScoreboardCache = mutableMapOf<GameTeam?, GameScoreboard>()
    private val playerScoreboardCache = mutableMapOf<Player, GameScoreboard>()
    private val setupTeams = mutableSetOf<GameTeam?>()

    override fun init(state: MinigameState) {
        // Generate scoreboards based on the current state
        teamService.allTeams.plus(null).forEach { team ->
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
            addOptionalStateInfo(scoreboard, state)

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

            teamScoreboardCache[team] = scoreboard
        }
    }

    override fun apply(player: Player, teamOfPlayer: GameTeam?) {
        if (minigameService.config.isTeamGame) {
            if (!setupTeams.contains(teamOfPlayer)) {
                if (teamOfPlayer == null) {
                    scoreboardService.setGlobalScoreboard(teamScoreboardCache[null])
                } else {
                    scoreboardService.setTeamScoreboard(teamOfPlayer.id, teamScoreboardCache[teamOfPlayer])
                }
                setupTeams.add(teamOfPlayer)
            }
        } else {
            scoreboardService.setPlayerScoreboard(player.uniqueId, playerScoreboardCache[player])
        }
    }

    override fun cleanup() {
        teamScoreboardCache.clear()
        playerScoreboardCache.clear()
    }

    fun addOptionalStateInfo(scoreboard: GameScoreboard, state: MinigameState) {
        // Map info
        if (state is IHasMapInfo) {
            scoreboard.addEntry(mm("<legacy_aqua><bold>Map:</bold></legacy_aqua> ${state.currentMapName}"))
            if (state.currentMapAuthor != null) {
                scoreboard.addEntry(mm("<legacy_aqua><bold>Map by:</bold></legacy_aqua> ${state.currentMapAuthor}"))
            }
        }

        // Round info
        if (state is IHasRoundInfo) {
            scoreboard.addEntry(mm("<legacy_green><bold>Round:</bold></legacy_green> ${state.currentRoundNumber}"))
        }

        // Game status
        if (state is IHasStateInfo) {
            scoreboard.addSpace()
            scoreboard.addEntry(mm("<legacy_red><bold>Game status:"))
            scoreboard.addEntry(mm(state.currentGameStatus))
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
