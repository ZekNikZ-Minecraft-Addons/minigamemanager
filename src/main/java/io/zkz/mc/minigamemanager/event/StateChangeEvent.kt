package io.zkz.mc.minigamemanager.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.minigamemanager.state.MinigameState
import org.bukkit.event.Cancellable

abstract class StateChangeEvent(
    val from: MinigameState,
    val to: MinigameState,
) : AbstractEvent() {
    class Pre(
        from: MinigameState,
        to: MinigameState,
    ) : StateChangeEvent(from, to), Cancellable {
        private var cancelled = false

        override fun isCancelled(): Boolean {
            return this.cancelled
        }

        override fun setCancelled(cancel: Boolean) {
            this.cancelled = cancel
        }
    }

    class Post(
        from: MinigameState,
        to: MinigameState,
    ) : StateChangeEvent(from, to)
}
