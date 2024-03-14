package io.zkz.mc.minigamemanager.state

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.get
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

interface ITracksAliveDead : InjectionComponent {
    val alivePlayers: Collection<UUID>

    val aliveTeams: Collection<GameTeam>

    val onlineAlivePlayers: Collection<Player>
        get() = alivePlayers.mapNotNull(Bukkit::getPlayer)

    fun setAlive(playerId: UUID)

    fun setAlive(player: Player)

    fun setDead(playerId: UUID)

    fun setDead(player: Player)

    fun isAlive(playerId: UUID): Boolean = alivePlayers.contains(playerId)

    fun isAlive(player: Player): Boolean = alivePlayers.contains(player.uniqueId)

    fun isTeamAlive(gameTeam: GameTeam?): Boolean {
        val teamService = get<TeamService>()
        return alivePlayers.any { teamService.getTeamOfPlayer(it) == gameTeam }
    }

    fun isTeamAlive(playerId: UUID): Boolean = isTeamAlive(get<TeamService>().getTeamOfPlayer(playerId))

    fun isTeamAlive(player: Player): Boolean = isTeamAlive(get<TeamService>().getTeamOfPlayer(player))
}
