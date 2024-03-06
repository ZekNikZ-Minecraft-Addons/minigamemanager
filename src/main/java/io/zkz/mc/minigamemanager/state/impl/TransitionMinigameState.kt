package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.MinigameState

class TransitionMinigameState(
    stateId: String,
    private val stateToTransitionTo: () -> MinigameState,
) : MinigameState(stateId), InjectionComponent {
    private val minigameService by inject<MinigameService>()

    override fun onEnter() {
        minigameService.setState(stateToTransitionTo())
    }
}
