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
    val postGameDelayInTicks: Int = 200,
    val shouldAutomaticallyShowRules: Boolean = false,
    val shouldShowScoreSummary: Boolean = true,
    val firstGameSpecificState: () -> MinigameState = { DefaultStates.POST_GAME },
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