package io.zkz.mc.minigamemanager.scoreboard

import io.zkz.mc.gametools.team.GameTeam
import org.bukkit.entity.Player

object EmptyMinigameScoreboard : MinigameScoreboard {
    override fun apply(player: Player, teamOfPlayer: GameTeam?) = Unit
}
