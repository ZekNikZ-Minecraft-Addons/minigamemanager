package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.base.PostGameState
import io.zkz.mc.minigamemanager.state.base.PostRoundState
import io.zkz.mc.minigamemanager.state.base.PreRoundState
import io.zkz.mc.minigamemanager.state.base.RulesState
import io.zkz.mc.minigamemanager.state.base.SetupState
import io.zkz.mc.minigamemanager.state.base.WaitingForPlayersState
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

    val SERVER_STARTING = register(DelegatedMinigameState("server_starting", "Server starting"))
    val SETUP = register(SetupState())

    val WAITING_FOR_PLAYERS = register(WaitingForPlayersState())
    val RULES = register(RulesState())
    val WAITING_TO_BEGIN = register(
        ReadyUpMinigameState({ PRE_ROUND }, "waiting_to_begin") {
            minigameService.config.shouldReadyUpEachRound
        },
    )

    val PRE_ROUND = register(PreRoundState())
    val IN_GAME = register(
        object : DelegatedMinigameState("in_game") {
            override val isInGame: Boolean = true
        },
    )
    val POST_ROUND = register(PostRoundState())

    val POST_GAME = register(PostGameState())
    val GAME_OVER = register(DelegatedMinigameState("game_over"))
    fun init() {
        ALL.forEach(minigameService::registerState)
    }
}
