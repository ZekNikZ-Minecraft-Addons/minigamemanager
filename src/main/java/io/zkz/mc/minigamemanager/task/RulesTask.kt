package io.zkz.mc.minigamemanager.task

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.sound.StandardSounds
import io.zkz.mc.gametools.sound.playSound
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.state.DefaultStates
import org.bukkit.Bukkit

class RulesTask : MinigameTask(TICK_DELAY, TICK_DELAY), InjectionComponent {
    companion object {
        const val TICK_DELAY = 160L
    }

    private val minigameService by inject<MinigameService>()

    private var currentIndex = 0

    override fun run() {
        if (currentIndex >= minigameService.config.rules.size) {
            return minigameService.setState(DefaultStates.WAITING_TO_BEGIN)
        }

        playSound(StandardSounds.ALERT_INFO, 1f, 1f)
        minigameService.config.rules[currentIndex].forEach(Bukkit.getServer()::sendMessage)

        ++currentIndex
    }
}
