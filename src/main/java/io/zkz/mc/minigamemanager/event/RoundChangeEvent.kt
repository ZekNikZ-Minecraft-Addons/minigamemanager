package io.zkz.mc.minigamemanager.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.minigamemanager.minigame.Round
import org.bukkit.event.Cancellable

abstract class RoundChangeEvent(
    val from: Round,
    val to: Round,
) : AbstractEvent() {
    class Pre(
        from: Round,
        to: Round,
    ) : RoundChangeEvent(from, to), Cancellable {
        private var cancelled = false

        override fun isCancelled(): Boolean {
            return this.cancelled
        }

        override fun setCancelled(cancel: Boolean) {
            this.cancelled = cancel
        }
    }

    class Post(
        from: Round,
        to: Round,
    ) : RoundChangeEvent(from, to)
}
