package io.zkz.mc.minigamemanager.state.impl

import io.zkz.mc.minigamemanager.state.IHasStateInfo

class BasicMinigameState(
    stateId: String,
    override val currentGameStatus: String,
) : DelegatedMinigameState(stateId), IHasStateInfo