package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.readyup.ReadyUpService
import io.zkz.mc.gametools.sound.StandardSounds
import io.zkz.mc.gametools.sound.playSound
import io.zkz.mc.gametools.timer.GameCountdownTimer
import io.zkz.mc.gametools.util.BukkitUtils
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

class PreRoundState : DelegatedMinigameState("pre_round") {
    private val readyUpService by inject<ReadyUpService>()

    override fun onEnter() {
        super.onEnter()

        // Reset glowing
        BukkitUtils.forEachPlayer { me ->
            BukkitUtils.forEachPlayer { other ->
                if (me != other) {
                    me.hidePlayer(minigameService.plugin, other)
                    me.showPlayer(minigameService.plugin, other)
                }
            }
        }

        if (minigameService.config.shouldReadyUpEachRound) {
            readyUpService.waitForReady(
                minigameService.participantsAndGameMasters,
                {
                    Chat.sendMessage(
                        ChatType.GAME_INFO,
                        mm("All players are now ready. Round starting in ${minigameService.config.preRoundDelayInTicks / 20} seconds."),
                    )
                    startTimer()
                },
            )
        } else {
            startTimer()
        }
    }

    override fun onExit() {
        super.onExit()

        minigameService.setGlobalTimer()
    }

    private fun startTimer() {
        val timer = GameCountdownTimer(
            minigameService.plugin,
            5,
            minigameService.config.preRoundDelayInTicks * 50L,
            DurationUnit.MILLISECONDS,
        ) {
            minigameService.setState(DefaultStates.IN_GAME)
        }

        timer.scheduleRepeatingEvent(0, 1000) { currentTime, _ ->
            if (currentTime <= 5000) {
                playSound(StandardSounds.TIMER_TICK, 1f, 1f)
            }
            val secondsLeft = (currentTime / 1000.0).roundToInt()
            minigameService.currentRound.onPreRoundTimerTick(secondsLeft)
        }

        minigameService.setGlobalTimer(timer, mm("Round starts in:"))
    }
}
