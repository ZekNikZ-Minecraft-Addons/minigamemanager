package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

open class DelegatedMinigameState(
    stateId: String,
) : MinigameState(stateId) {
    private val onEnterDelegates: MutableList<MinigameState.() -> Unit> = mutableListOf()
    private val onExitDelegates: MutableList<MinigameState.() -> Unit> = mutableListOf()
    private val onPlayerJoinDelegates: MutableList<MinigameState.(player: Player) -> Unit> = mutableListOf()
    private val onPlayerQuitDelegates: MutableList<MinigameState.(player: Player) -> Unit> = mutableListOf()
    private val onPlayerDeathDelegates: MutableList<MinigameState.(player: Player) -> Unit> = mutableListOf()

    override fun onEnter() {
        onEnterDelegates.forEach { it() }
    }

    fun handleEnter(handler: MinigameState.() -> Unit) {
        onEnterDelegates.add(handler)
    }

    override fun onExit() {
        onExitDelegates.forEach { it() }
    }

    fun handleExit(handler: MinigameState.() -> Unit) {
        onExitDelegates.add(handler)
    }

    override fun onPlayerJoin(player: Player) {
        onPlayerJoinDelegates.forEach { it(player) }
    }

    fun handlePlayerJoin(handler: MinigameState.(player: Player) -> Unit) {
        onPlayerJoinDelegates.add(handler)
    }

    override fun onPlayerQuit(player: Player) {
        onPlayerQuitDelegates.forEach { it(player) }
    }

    fun handlePlayerQuit(handler: MinigameState.(player: Player) -> Unit) {
        onPlayerQuitDelegates.add(handler)
    }

    override fun onPlayerDeath(player: Player) {
        onPlayerDeathDelegates.forEach { it(player) }
    }

    fun handlePlayerDeath(handler: MinigameState.(player: Player) -> Unit) {
        onPlayerDeathDelegates.add(handler)
    }
}
