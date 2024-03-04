package io.zkz.mc.minigamemanager.scoreboard

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.score.ScoreService
import io.zkz.mc.gametools.scoreboard.entry.ScoreboardEntry
import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.gametools.teams.TeamService
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.gametools.util.observable.IObservable
import io.zkz.mc.gametools.util.observable.IObserver
import java.util.concurrent.atomic.AtomicInteger

class TeamScoresScoreboardEntry(
    private val team: GameTeam?,
) : ScoreboardEntry(), IObserver<ScoreService>, InjectionComponent {
    private val scoreService by inject<ScoreService>()
    private val teamService by inject<TeamService>()

    init {
        scoreService.addListener(this)
    }

    override fun render(pos: Int) {
        // Header
        scoreboard.setLine(
            pos,
            mm("<legacy_aqua><bold>Game Points:</bold></legacy_aqua> (<legacy_yellow>${scoreService.currentScoreMultiplier}x</legacy_yellow>)"),
        )

        // Get score entries
        val entries = scoreService
            .query().forAllTeams().inGame().get()
            .entries
            .sortedWith(compareBy({ -it.value }, { it.key }))
        if (entries.isEmpty()) {
            return
        }

        // Get team placement
        var placement = -1
        val totalNumTeams = entries.size
        if (team != null) {
            placement = entries.indexOfFirst { it.key == team.id }
        }

        // Determine which teams to display
        val placements: List<Int> = if (placement <= 1) { // team is in first place
            listOf(0, 1, 2, 3)
        } else if (placement == totalNumTeams - 1) { // team is in last place
            listOf(0, totalNumTeams - 3, totalNumTeams - 2, totalNumTeams - 1)
        } else { // team is in the middle
            listOf(0, placement - 1, placement, placement + 1)
        }

        // Write to scoreboard
        val i = AtomicInteger(1)
        placements.forEach {
            displayScore(
                pos + i.getAndIncrement(),
                it,
                entries[it],
            )
        }
    }

    private fun displayScore(scoreboardPos: Int, placement: Int, entry: Map.Entry<String?, Double>) {
        // TODO: add padding
        scoreboard.setLine(scoreboardPos, mm("${placement + 1}. ${teamService.getTeam(entry.key!!)!!.displayName} ${entry.value.toInt()}${ChatType.Constants.POINT_CHAR}"))
    }

    override val rowCount: Int
        get() = 5

    override fun cleanup() {
        scoreService.removeListener(this)
    }

    override fun handleChanged(observable: IObservable<ScoreService>) {
        markDirty()
    }
}
