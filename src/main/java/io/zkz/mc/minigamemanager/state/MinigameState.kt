package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.minigamemanager.minigame.MinigameService
import io.zkz.mc.minigamemanager.scoreboard.MinigameScoreboard
import io.zkz.mc.minigamemanager.scoreboard.impl.StandardMinigameScoreboard
import io.zkz.mc.minigamemanager.task.MinigameTask
import org.bukkit.entity.Player

open class MinigameState(
    val id: String,
    val gameStatusString: String? = null,
) : InjectionComponent {
    protected val minigameService by inject<MinigameService>()

    val isActive
        get() = minigameService.currentState == this

    open val isInGame
        get() = false

    /**
     * Initialize this state after it is registered.
     */
    open fun init() {
        onSetup()
    }

    /**
     * Called during state initialization.
     */
    open fun onSetup() = Unit

    /**
     * Called when entering this state.
     */
    open fun onEnter() = Unit

    /**
     * Called when exiting this state.
     */
    open fun onExit() = Unit

    /**
     * Called when a player joins the server while this state is active.
     */
    open fun onPlayerJoin(player: Player) = Unit

    /**
     * Called when a player quits the server while this state is active.
     */
    open fun onPlayerQuit(player: Player) = Unit

    /**
     * Called when a player dies while this state is active.
     */
    open fun onPlayerDeath(player: Player) = Unit

    /**
     * Build the minigame scoreboard for this state.
     */
    open fun buildScoreboard(): MinigameScoreboard? = StandardMinigameScoreboard

    fun addTask(delay: Long, task: () -> Unit) {
        if (!isActive) {
            throw IllegalStateException("You can only add tasks while the state is active")
        }

        addTask(MinigameTask.from(delay, null, task))
    }

    fun addTask(delay: Long, period: Long, task: () -> Unit) {
        if (!isActive) {
            throw IllegalStateException("You can only add tasks while the state is active")
        }

        addTask(MinigameTask.from(delay, period, task))
    }

    fun addTask(task: MinigameTask) {
        if (!isActive) {
            throw IllegalStateException("You can only add tasks while the state is active")
        }

        minigameService.registerTask(task)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MinigameState) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
