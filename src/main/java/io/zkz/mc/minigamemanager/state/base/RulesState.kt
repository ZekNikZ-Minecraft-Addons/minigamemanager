package io.zkz.mc.minigamemanager.state.base

import io.zkz.mc.minigamemanager.state.impl.DelegatedMinigameState
import io.zkz.mc.minigamemanager.task.RulesTask

class RulesState : DelegatedMinigameState("rules", "Showing rules") {
    override fun onEnter() {
        super.onEnter()
        addTask(RulesTask())
    }
}
