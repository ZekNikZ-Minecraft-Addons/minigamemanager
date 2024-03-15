package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.readyup.ReadyUpService
import io.zkz.mc.gametools.readyup.ReadyUpSession
import io.zkz.mc.gametools.scoreboard.ScoreboardService
import io.zkz.mc.gametools.scoreboard.entry.ValueEntry
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.minigamemanager.scoreboard.impl.StandardMinigameScoreboard
import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.entity.Player

class ReadyUpMinigameState(
    private val nextState: () -> MinigameState,
    stateId: String = "waiting_for_ready",
    private val skipReady: () -> Boolean = { false },
) : MinigameState(stateId), InjectionComponent {
    private val readyUpService by inject<ReadyUpService>()
    private val scoreboardService by inject<ScoreboardService>()

    override fun onEnter() {
        if (skipReady()) {
            runNextTick {
                minigameService.setState(nextState())
            }
        }

        readyUpService.waitForReady(minigameService.participantsAndGameMasters, ::onAllReady, ::onPlayerReady)
    }

    private fun onAllReady() {
        minigameService.setState(nextState())
    }

    private fun onPlayerReady(player: Player, session: ReadyUpSession) {
        scoreboardService.allScoreboards.forEach { scoreboard ->
            val entry: ValueEntry<Int>? = scoreboard.getEntry(StandardMinigameScoreboard.KEY_READY_PLAYER_COUNT)
            entry?.value = session.readyPlayerCount.toInt()
        }
    }
}
