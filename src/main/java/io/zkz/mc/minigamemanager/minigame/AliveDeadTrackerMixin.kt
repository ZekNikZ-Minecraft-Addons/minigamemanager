package io.zkz.mc.minigamemanager.minigame

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import org.bukkit.entity.Player
import java.util.*

class AliveDeadTrackerMixin : ITracksAliveDead {
    private val teamService by inject<TeamService>()

    private val _alivePlayers: MutableSet<UUID> = mutableSetOf()
    override val alivePlayers: Set<UUID>
        get() = _alivePlayers

    override val aliveTeams: Set<GameTeam>
        get() = _alivePlayers.mapNotNull(teamService::getTeamOfPlayer).toSet()

    override fun setAlive(playerId: UUID) {
        _alivePlayers.add(playerId)
    }

    override fun setAlive(player: Player) = setAlive(player.uniqueId)

    override fun setDead(playerId: UUID) {
        _alivePlayers.remove(playerId)
    }

    override fun setDead(player: Player) = setDead(player.uniqueId)
}
