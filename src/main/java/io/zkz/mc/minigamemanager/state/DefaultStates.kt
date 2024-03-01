package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.impl.BasicMinigameState

@Injectable
object DefaultStates : InjectionComponent {
    private val minigameService by inject<MinigameService>()

    val SERVER_STARTING = minigameService.registerState(BasicMinigameState("server_starting", "Server starting..."))
    val LOADING = minigameService.registerState(BasicMinigameState("loading", "Loading..."))
    val SETUP = minigameService.registerState(DelegatedMinigameState("setup"))

    val WAITING_FOR_PLAYERS = minigameService.registerState(MinigameState("waiting_for_players"))
    val RULES = minigameService.registerState(MinigameState("rules"))
    val WAITING_TO_BEGIN = minigameService.registerState(MinigameState("waiting_to_begin"))

    val PRE_GAME = minigameService.registerState(DelegatedMinigameState("pre_game"))
    val POST_GAME = minigameService.registerState(DelegatedMinigameState("post_game"))
}