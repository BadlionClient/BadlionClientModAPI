package net.badlion.modapicommon.utility;

import java.util.List;
import java.util.UUID;

public abstract class AbstractTeamMarkerManager {

	public abstract void sendLocations(UUID player, List<TeamMemberLocation> locations);

}
