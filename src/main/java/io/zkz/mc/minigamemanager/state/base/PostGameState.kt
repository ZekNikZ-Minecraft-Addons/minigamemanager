package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.gametools.timer.GameCountdownTimer
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import io.zkz.mc.minigamemanager.task.ScoreSummaryTask
import kotlin.time.DurationUnit

class PostGameState : DelegatedMinigameState("post_game") {
    override fun onEnter() {
        super.onEnter()
        minigameService.setGlobalTimer(
            GameCountdownTimer(
                minigameService.plugin,
                20,
                minigameService.config.postGameDelayInTicks * 50L + ScoreSummaryTask.SECONDS_PER_SLIDE * ScoreSummaryTask.NUM_SLIDES * 20L,
                DurationUnit.MILLISECONDS,
            ) {
                minigameService.setState(DefaultStates.GAME_OVER)
            },
            mm("Back to hub in:"),
        )

        addTask(ScoreSummaryTask())
    }
}
