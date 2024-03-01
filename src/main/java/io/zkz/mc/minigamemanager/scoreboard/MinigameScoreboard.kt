package io.zkz.mc.minigamemanager.scoreboard

import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

/**
 * Wrapper to generate multiple distinct scoreboards in one place. Use `init()` to initialize scoreboards/cache
 * if desired. `setup()` will be called once per player with that player's team.
 */
interface MinigameScoreboard {
    fun init(state: MinigameState) = Unit

    fun apply(player: Player, teamOfPlayer: GameTeam?)

    fun cleanup() = Unit
}