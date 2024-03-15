package io.zkz.mc.minigamemanager.scoreboard.impl

import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.minigamemanager.scoreboard.MinigameScoreboard
import org.bukkit.entity.Player

object EmptyMinigameScoreboard : MinigameScoreboard {
    override fun apply(player: Player, teamOfPlayer: GameTeam?) = Unit
}
