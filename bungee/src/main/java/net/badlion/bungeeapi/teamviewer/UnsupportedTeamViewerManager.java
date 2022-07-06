package net.badlion.bungeeapi.teamviewer;

import net.badlion.modapicommon.utility.AbstractTeamViewerManager;
import net.badlion.modapicommon.utility.AbstractWaypoint;
import net.badlion.modapicommon.utility.AbstractWaypointManager;
import net.badlion.modapicommon.utility.TeamMemberLocation;

import java.util.List;
import java.util.UUID;

public class UnsupportedTeamViewerManager extends AbstractTeamViewerManager {

	@Override
	public void sendLocations(UUID player, List<TeamMemberLocation> locations) {
		throw new UnsupportedOperationException("Team Viewer mod hooks are not supported on bungee!");
	}
}
