package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState

abstract class Round(
    /**
     * The name of the map used for the round. Null if not used.
     */
    open var mapName: String? = null,
    /**
     * The author of the map used for the round. Null if not used.
     */
    open var mapBy: String? = null,
) : ITracksAliveDead by AliveDeadTrackerMixin() {
    private val stateHandlers = mutableMapOf<String, MutableList<DelegatedMinigameState.() -> Unit>>()

    /**
     * Register a callback for when we transition to the specified state while this
     * round is active.
     */
    protected fun <T : DelegatedMinigameState> handleEnter(state: T, callback: T.() -> Unit) {
        stateHandlers.putIfAbsent(state.id, mutableListOf())
        @Suppress("UNCHECKED_CAST")
        stateHandlers[state.id]!!.add(callback as DelegatedMinigameState.() -> Unit)
    }

    /**
     * Callback for when a state transition occurs while this state is selected.
     */
    fun onEnterState(state: DelegatedMinigameState) {
        stateHandlers[state.id]?.forEach {
            state.it()
        }
    }

    /**
     * Called when this round gets created to register state handlers using `handleEnter()`.
     */
    open fun onSetup() = Unit

    /**
     * Callback for when this round gets selected.
     */
    open fun onSelected() = Unit

    /**
     * Callback for when another round gets selected while this one is currently active.
     */
    open fun onDeselected() = Unit

    /**
     * Callback for pre-round timer ticks.
     */
    open fun onPreRoundTimerTick(secondsLeft: Int) = Unit
}
