package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.gametools.timer.GameCountdownTimer
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import kotlin.time.DurationUnit

class PostRoundState : DelegatedMinigameState("post_round") {
    override fun onEnter() {
        super.onEnter()

        minigameService.setGlobalTimer()

        if (minigameService.config.shouldAutomaticallyGoToNextRound) {
            if (minigameService.isLastRound) {
                runNextTick {
                    minigameService.setState(DefaultStates.POST_GAME)
                }
            } else {
                minigameService.setGlobalTimer(
                    GameCountdownTimer(
                        minigameService.plugin,
                        20,
                        minigameService.config.postRoundDelayInTicks * 50L,
                        DurationUnit.MILLISECONDS,
                    ) {
                        minigameService.setRound(minigameService.currentRoundIndex + 1)
                        minigameService.setState(DefaultStates.PRE_ROUND)
                    },
                    mm("Next round in:"),
                )
            }
        }
    }
}
