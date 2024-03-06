package io.zkz.mc.minigamemanager.task

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.minigamemanager.minigame.MinigameService

class ScoreSummaryTask : MinigameTask(SECONDS_PER_SLIDE * 20L, SECONDS_PER_SLIDE * 20L), InjectionComponent {
    companion object {
        const val NUM_SLIDES = 4
        const val SECONDS_PER_SLIDE = 5
    }

    private val minigameService by inject<MinigameService>()

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
