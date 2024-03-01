package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.minigamemanager.MinigameManagerPlugin
import io.zkz.mc.minigamemanager.event.StateChangeEvent
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.MinigameState

@Injectable
class MinigameService(
    plugin: MinigameManagerPlugin,
) : PluginService<MinigameManagerPlugin>(plugin) {
    private val states: MutableMap<String, MinigameState> = mutableMapOf()
    var currentState: MinigameState = DefaultStates.SERVER_STARTING
        private set

    fun <T : MinigameState> registerState(state: T, replace: Boolean = false): T {
        if (!replace && states.containsKey(state.id)) {
            throw IllegalStateException("State with ID ${state.id} already exists")
        }

        states[state.id] = state
        state.init()
        return state
    }

    fun <T : MinigameState> registerState(stateBuilder: () -> T): T {
        return stateBuilder().also { registerState(it) }
    }

    fun setState(newState: MinigameState) {
        val oldState = currentState
        val preEvent = StateChangeEvent.Pre(oldState, newState)
        event(preEvent)

        if (preEvent.isCancelled) {
            return
        }

        currentState.onExit()
        currentState = newState
        newState.onEnter()

        event(StateChangeEvent.Post(oldState, newState))
    }

    override fun onEnable() {
        runNextTick {
            setState(DefaultStates.SETUP)
        }
    }
}