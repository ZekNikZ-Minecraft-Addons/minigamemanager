package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.gametools.util.PlayerUtils.allOnline
import io.zkz.mc.gametools.util.PlayerUtils.filterOnline
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState

class WaitingForPlayersState : DelegatedMinigameState("waiting_for_players", "Waiting for players") {
    private val scoreboardService by inject<ScoreboardService>()

    override fun onEnter() {
        addTask(1, 20, ::waitForPlayers)
    }

    private fun waitForPlayers() {
        // Update scoreboards
        val numReadyParticipants = minigameService.participants.filterOnline().count()
        scoreboardService.allScoreboards.forEach { scoreboard ->
            val entry = scoreboard.getEntry<ValueEntry<Int>>("playerCount")
            entry?.value = numReadyParticipants
        }

        // Check if we should move on
        if (minigameService.config.shouldAutomaticallyShowRules && minigameService.participants.allOnline()) {
            minigameService.setState(DefaultStates.RULES)
        }
    }
}
