package net.badlion.bukkitapi.teammarker;

import net.badlion.bukkitapi.AbstractBukkitBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.mods.ModType;
import net.badlion.modapicommon.utility.AbstractTeamMarkerManager;
import net.badlion.modapicommon.utility.TeamMemberLocation;

import java.util.List;
import java.util.UUID;

public class TeamMarkerManager extends AbstractTeamMarkerManager {

	private final AbstractBukkitBadlionPlugin apiBukkit;

	public TeamMarkerManager(AbstractBukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	@Override
	public void sendLocations(UUID player, List<TeamMemberLocation> locations) {
		this.apiBukkit.getMessageSender().sendModData(player, ModType.TEAM_MARKER, AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(locations));
	}
}
