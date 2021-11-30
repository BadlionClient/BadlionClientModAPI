package net.badlion.modapicommon.mods;

import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.Location;
import net.badlion.modapicommon.utility.Waypoint;

import java.util.UUID;

/**
 * Class with API Hooks for our Waypoints mod.
 */
public class Waypoints {
	private static int currentIndexId = 0;

	/**
	 * Creates a waypoint at a specified location for an online Badlion user.
	 *
	 * @param player     The player this waypoint should be added for.
	 * @param location   The location for this waypoint.
	 * @param text       The text this waypoint has when not looked at, supports color codes.
	 * @param allowToAdd Whether the client should ask the user if they wish to add this waypoint to the mod.
	 * @return The waypoint that was created for the user.
	 */
	public static Waypoint createWaypoint(UUID player, Location location, String text, boolean allowToAdd) {
		int id = Waypoints.currentIndexId++;

		final Waypoint waypoint = new Waypoint(id, location, text, allowToAdd);
		AbstractBadlionApi.getInstance().getWaypointManager().addPlayerWaypoint(player, waypoint);

		return waypoint;
	}

	/**
	 * Deletes a waypoint by a specific ID.
	 *
	 * @param id ID of the waypoint to delete.
	 */
	public static void deleteWaypoint(UUID player, int id) {
		AbstractBadlionApi.getInstance().getWaypointManager().removePlayerWaypoint(player, id);
	}
}