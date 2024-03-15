package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.GTConstants
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.MinigameState
import net.kyori.adventure.text.Component

data class MinigameConfig(
    val isTeamGame: Boolean = true,
    val rules: List<List<Component>> = listOf(),
    private val name: String? = null,
    private val tournament: String? = null,
    val currentGameNumber: Int? = null,
    val maxGameNumber: Int? = null,
    val preRoundDelayInTicks: Int = 200,
    val postRoundDelayInTicks: Int = 200,
    val postGameDelayInTicks: Int = 200,
    val shouldAutomaticallyShowRules: Boolean = false,
    val shouldAutomaticallyGoToNextRound: Boolean = true,
    val shouldShowScoreSummary: Boolean = true,
    val shouldReadyUpEachRound: Boolean = false,
    val firstGameSpecificState: () -> MinigameState = { DefaultStates.IN_GAME },
) : InjectionComponent {
    private val constants by inject<GTConstants>()

    val minigameName
        get() = name ?: constants.gameName

    val tournamentName
        get() = tournament ?: constants.gameName

    companion object {
        val DEFAULT_CONFIG = MinigameConfig()
    }
}
