package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.minigamemanager.state.MinigameState

open class ParentMinigameState(
    stateId: String,
) : MinigameState(stateId) {
    private val childStates: MutableList<ParentMinigameState> = mutableListOf()

    /**
     * Initialize this state after it is registered.
     */
    override fun init() {
        buildChildStates()
        onSetup()
    }

    /**
     * Add a child to this state.
     * The first child added will be the default child state.
     */
    protected fun addChild(state: ParentMinigameState) {
        state.parentState = this
        state.init()
        childStates.add(state)
    }

    /**
     * Override this function to add child states to this state.
     * Use the `addChild()` method to add children.
     * The first child added will be the default child state.
     */
    open fun buildChildStates() = Unit
}