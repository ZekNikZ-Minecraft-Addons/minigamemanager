package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.minigamemanager.state.DefaultStates
import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState

class SetupState : DelegatedMinigameState("setup") {
    override fun onEnter() {
        super.onEnter()
        minigameService.setRound(0)
        runNextTick {
            minigameService.setState(DefaultStates.WAITING_FOR_PLAYERS)
        }
    }
}
