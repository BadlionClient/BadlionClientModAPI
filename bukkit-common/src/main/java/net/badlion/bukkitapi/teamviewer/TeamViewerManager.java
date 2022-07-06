package net.badlion.bukkitapi.teamviewer;

import net.badlion.bukkitapi.AbstractBukkitBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.mods.ModType;
import net.badlion.modapicommon.utility.AbstractTeamViewerManager;
import net.badlion.modapicommon.utility.TeamMemberLocation;

import java.util.List;
import java.util.UUID;

public class TeamViewerManager extends AbstractTeamViewerManager {

	private final AbstractBukkitBadlionPlugin apiBukkit;

	public TeamViewerManager(AbstractBukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	@Override
	public void sendLocations(UUID player, List<TeamMemberLocation> locations) {
		this.apiBukkit.getMessageSender().sendModData(player, ModType.TEAM_VIEWER, AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(locations));
	}
}
