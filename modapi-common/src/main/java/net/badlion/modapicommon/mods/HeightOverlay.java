package net.badlion.modapicommon.mods;

import com.google.gson.JsonObject;
import net.badlion.modapicommon.AbstractBadlionApi;

import java.util.UUID;

/**
 * Class with API Hooks for our Height Overlay mod.
 */
public class HeightOverlay {

	/**
	 * Sets the height limit of a map and starts showing the height overlay mod for every online Badlion user.
	 *
	 * @param mapName   The name of the map
	 * @param maxHeight The height limit
	 */
	public static void setCurrentMap(String mapName, int maxHeight) {
		final JsonObject data = new JsonObject();
		data.addProperty("mapName", mapName);
		data.addProperty("maxHeight", maxHeight);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData("heightOverlay", data);
	}

	/**
	 * Sets the height limit of a map and starts showing the height overlay mod for a specific Badlion user.
	 *
	 * @param player    The player to set this for.
	 * @param mapName   The name of the map
	 * @param maxHeight The height limit
	 */
	public static void setCurrentMap(UUID player, String mapName, int maxHeight) {
		final JsonObject data = new JsonObject();
		data.addProperty("mapName", mapName);
		data.addProperty("maxHeight", maxHeight);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(player, "heightOverlay", data);
	}

	/**
	 * Resets the height overlay limit and stops displaying the mod for every online Badlion user.
	 * This should be used when you no longer want to show the mod for every online Badlion user.
	 */
	public static void reset() {
		final JsonObject data = new JsonObject();
		data.addProperty("mapName", "");
		data.addProperty("maxHeight", -1);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData("heightOverlay", data);
	}

	/**
	 * Resets the height overlay limit and stops displaying the mod for a specific online Badlion user.
	 * This should be used when you no longer want to show the mod for the user.
	 */
	public static void reset(UUID player) {
		final JsonObject data = new JsonObject();
		data.addProperty("mapName", "");
		data.addProperty("maxHeight", -1);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(player, "heightOverlay", data);
	}
}