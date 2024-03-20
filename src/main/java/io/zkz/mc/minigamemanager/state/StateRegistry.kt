package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService

abstract class StateRegistry : InjectionComponent {
    protected val minigameService by inject<MinigameService>()

    private val registeredStates = mutableListOf<MinigameState>()

    protected fun <T : MinigameState> register(state: T): T {
        registeredStates.add(state)
        return state
    }

    fun init() {
        registeredStates.forEach(minigameService::registerState)
    }
}
