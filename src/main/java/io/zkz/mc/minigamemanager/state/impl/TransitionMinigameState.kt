package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.minigamemanager.state.MinigameState

class TransitionMinigameState(
    stateId: String,
    private val stateToTransitionTo: () -> MinigameState,
) : MinigameState(stateId), InjectionComponent {
    override fun onEnter() {
        minigameService.setState(stateToTransitionTo())
    }
}
