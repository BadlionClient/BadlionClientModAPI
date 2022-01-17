package net.badlion.modapicommon.utility;

import com.google.gson.JsonObject;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.mods.ModType;

import java.util.UUID;

/**
 * Handles hooks into the Badlion Waypoints mod for players.
 */
public abstract class AbstractWaypointManager {

	/**
	 * Adds a waypoint for a specific player if the player allows it, and has the mod enabled.
	 *
	 * @param player   UUID of the player
	 * @param waypoint The waypoint object to add.
	 */
	public abstract void addPlayerWaypoint(UUID player, AbstractWaypoint waypoint);

	/**
	 * Removes a waypoint for a specific player.
	 *
	 * @param player UUID of the player
	 * @param id     The waypoint ID to delete as given from {@link net.badlion.modapicommon.mods.Waypoints#createWaypoint(UUID, Location, String, boolean)}
	 */
	public abstract void removePlayerWaypoint(UUID player, int id);

	/**
	 * Resets every waypoint given from the API.
	 *
	 * @param player UUID of the player
	 */
	public void resetWaypoints(UUID player) {
		final JsonObject data = new JsonObject();
		data.addProperty("action", "reset");

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(player, ModType.WAYPOINTS, data);
	}
}