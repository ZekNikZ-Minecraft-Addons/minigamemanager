package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.impl.BasicMinigameState
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import io.zkz.mc.minigamemanager.state.impl.ReadyUpMinigameState

@Injectable
object DefaultStates : InjectionComponent {
    private val minigameService by inject<MinigameService>()

    private val ALL = mutableListOf<MinigameState>()
    private fun <T : MinigameState> register(state: T): T {
        ALL.add(state)
        return state
    }

    val SERVER_STARTING = register(BasicMinigameState("server_starting", "Server starting..."))
    val SETUP = register(DelegatedMinigameState.WithStateInfo("setup", "Server loading..."))

    val WAITING_FOR_PLAYERS = register(MinigameState("waiting_for_players"))
    val RULES = register(MinigameState("rules"))
    val WAITING_TO_BEGIN = register(ReadyUpMinigameState({ PRE_GAME }, "waiting_to_begin"))

    val PRE_GAME = register(DelegatedMinigameState("pre_game"))
    val POST_GAME = register(DelegatedMinigameState("post_game"))

    fun init() {
        ALL.forEach(minigameService::registerState)
    }
}
