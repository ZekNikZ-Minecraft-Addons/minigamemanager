package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

open class DelegatedMinigameState(
    stateId: String,
) : MinigameState(stateId) {
    private val onEnterDelegates: MutableList<() -> Unit> = mutableListOf()
    private val onExitDelegates: MutableList<() -> Unit> = mutableListOf()
    private val onPlayerJoinDelegates: MutableList<(player: Player) -> Unit> = mutableListOf()
    private val onPlayerQuitDelegates: MutableList<(player: Player) -> Unit> = mutableListOf()
    private val onPlayerDeathDelegates: MutableList<(player: Player) -> Unit> = mutableListOf()

    override fun onEnter() {
        onEnterDelegates.forEach { it() }
    }

    override fun onExit() {
        onExitDelegates.forEach { it() }
    }

    override fun onPlayerJoin(player: Player) {
        onPlayerJoinDelegates.forEach { it(player) }
    }

    override fun onPlayerQuit(player: Player) {
        onPlayerQuitDelegates.forEach { it(player) }
    }

    override fun onPlayerDeath(player: Player) {
        onPlayerDeathDelegates.forEach { it(player) }
    }
}
