package io.zkz.mc.minigamemanager.task

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.GameTask
import io.zkz.mc.minigamemanager.minigame.MinigameService

abstract class MinigameTask(
    delay: Long,
    period: Long? = null,
) : GameTask(delay, period), InjectionComponent {
    private val minigameService by inject<MinigameService>()

    override fun cancel(removeReference: Boolean) {
        super.cancel(removeReference)
        if (removeReference) {
            minigameService.removeRunningTask(this)
        }
    }

    companion object {
        fun from(delay: Long, period: Long? = null, task: () -> Unit): MinigameTask {
            return object : MinigameTask(delay, period) {
                override fun run() {
                    task()
                }

            }
        }
    }
}