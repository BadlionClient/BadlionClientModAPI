package net.badlion.modapicommon.mods;

import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.TeamMemberLocation;

import java.util.List;
import java.util.UUID;

/**
 * Class with API Hooks for our Team Viewer mod.
 */
public class TeamViewer {

	/**
	 * Update the team member locations. You always need to send all the locations that should be showing as on each call old points will be deleted
	 *
	 * The client automatically removes locations that weren't updated for 5 seconds, so we recommend sending the updates faster than this.
	 * If the team member can be tracked by the client it will also automatically update the location but above timeout still applies.
	 *
	 * @param player UUID from the player who should be receiving this
	 * @param locations List of team member locations to show
	 */
	public static void sendLocations(UUID player, List<TeamMemberLocation> locations) {
		AbstractBadlionApi.getInstance().getTeamViewerManager().sendLocations(player, locations);
	}
}
