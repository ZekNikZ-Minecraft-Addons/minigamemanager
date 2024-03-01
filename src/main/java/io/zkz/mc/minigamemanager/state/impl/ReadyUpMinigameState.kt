package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.readyup.ReadyUpService
import io.zkz.mc.gametools.readyup.ReadyUpSession
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.scoreboard.StandardMinigameScoreboard
import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

class ReadyUpMinigameState(
    private val nextState: () -> MinigameState,
    stateId: String = "waiting_for_ready",
) : MinigameState(stateId), InjectionComponent {
    private val minigameService by inject<MinigameService>()
    private val readyUpService by inject<ReadyUpService>()
    private val scoreboardService by inject<ScoreboardService>()

    override fun onEnter() {
        readyUpService.waitForReady(minigameService.participantsAndGameMasters, ::onAllReady, ::onPlayerReady)
    }

    private fun onAllReady() {
        minigameService.setState(nextState())
    }

    @Suppress("UNCHECKED_CAST")
    private fun onPlayerReady(player: Player, session: ReadyUpSession) {
        scoreboardService.allScoreboards.forEach { scoreboard ->
            val entry = scoreboard.getEntry(StandardMinigameScoreboard.KEY_READY_PLAYER_COUNT)
            if (entry is ValueEntry<*>) {
                (entry as ValueEntry<Int>).value = session.readyPlayerCount.toInt()
            }
        }
    }
}
