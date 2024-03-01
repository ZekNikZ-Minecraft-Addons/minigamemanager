package io.zkz.mc.minigamemanager.state

import org.bukkit.entity.Player

open class MinigameState(
    private val stateId: String,
) {
    var parentState: MinigameState? = null
        internal set
    val id: String
        get() = "${if (parentState != null) "${parentState!!.id}/" else ""}${stateId}"

    /**
     * Initialize this state after it is registered.
     */
    open fun init() {
        onSetup()
    }

    /**
     * Called during state initialization.
     */
    open fun onSetup() = Unit

    /**
     * Called when entering this state.
     */
    open fun onEnter() = Unit

    /**
     * Called when exiting this state.
     */
    open fun onExit() = Unit

    /**
     * Called when a player joins the server while this state is active.
     */
    open fun onPlayerJoin(player: Player) = Unit

    /**
     * Called when a player quits the server while this state is active.
     */
    open fun onPlayerQuit(player: Player) = Unit

    /**
     * Called when a player dies while this state is active.
     */
    open fun onPlayerDeath(player: Player) = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MinigameState) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}