package net.badlion.velocityapi.teammarker;

import net.badlion.modapicommon.utility.AbstractTeamMarkerManager;
import net.badlion.modapicommon.utility.TeamMemberLocation;

import java.util.List;
import java.util.UUID;

public class UnsupportedTeamMarkerManager extends AbstractTeamMarkerManager {

	@Override
	public void sendLocations(UUID player, List<TeamMemberLocation> locations) {
		throw new UnsupportedOperationException("Team Marker mod hooks are not supported on bungee!");
	}
}
