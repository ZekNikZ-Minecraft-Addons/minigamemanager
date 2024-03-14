package io.zkz.mc.minigamemanager.task

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.util.Chat

class ScoreSummaryTask : MinigameTask(SECONDS_PER_SLIDE * 20L, SECONDS_PER_SLIDE * 20L), InjectionComponent {
    companion object {
        const val NUM_SLIDES = 4
        const val SECONDS_PER_SLIDE = 5
    }

    private var currentSlideIndex = 0

    override fun run() {
        if (!minigameService.config.shouldShowScoreSummary) {
            return this.cancel()
        }

        Chat.sendEmptyLine()
        when (currentSlideIndex) {
            // TODO:
        }

        ++currentSlideIndex
    }
}
