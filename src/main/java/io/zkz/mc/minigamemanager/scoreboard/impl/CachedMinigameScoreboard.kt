package io.zkz.mc.minigamemanager.scoreboard.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.scoreboard.GameScoreboard
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.scoreboard.MinigameScoreboard
import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

abstract class CachedMinigameScoreboard : MinigameScoreboard, InjectionComponent {
    private val minigameService by inject<MinigameService>()
    private val teamService by inject<TeamService>()
    private val scoreboardService by inject<ScoreboardService>()

    private val teamScoreboardCache = mutableMapOf<GameTeam?, GameScoreboard>()
    private val setupTeams = mutableSetOf<GameTeam?>()

    override fun init(state: MinigameState) {
        // Generate scoreboards based on the current state
        if (minigameService.config.isTeamGame) {
            teamService.allTeams.plus(null).forEach {
                val scoreboard =
                    buildScoreboard(it, state) ?: throw IllegalStateException("Cached scoreboard cannot be null")
                teamScoreboardCache[it] = scoreboard
            }
        }
    }

    abstract fun buildScoreboard(team: GameTeam?, state: MinigameState): GameScoreboard?

    abstract fun buildScoreboard(player: Player, teamOfPlayer: GameTeam?, state: MinigameState): GameScoreboard?

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
            scoreboardService.setPlayerScoreboard(
                player.uniqueId,
                buildScoreboard(player, teamOfPlayer, minigameService.currentState),
            )
        }
    }

    override fun cleanup() {
        teamScoreboardCache.clear()
    }
}
