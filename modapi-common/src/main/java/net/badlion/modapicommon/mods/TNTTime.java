package net.badlion.modapicommon.mods;

import com.google.gson.JsonObject;
import net.badlion.modapicommon.AbstractBadlionApi;

import java.util.UUID;

/**
 * Class with API Hooks for our TNTTime mod.
 */
public class TNTTime {

	/**
	 * Sets the TNTTime fuse counter offset for every online Badlion user.
	 *
	 * @param offset Fuse ticks to add or subtract from 80 (vanilla fuse time value)
	 */
	public static void setFuseOffset(int offset) {
		final JsonObject data = new JsonObject();
		data.addProperty("fuseOffset", offset);
		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(ModType.TNT_TIME, data);
	}

	/**
	 * Sets the TNTTime fuse counter offset for a specific Badlion user.
	 *
	 * @param player The player who will receive the counter
	 * @param offset Fuse ticks to add or subtract from 80 (vanilla fuse time value)
	 */
	public static void setFuseOffset(UUID player, int offset) {
		final JsonObject data = new JsonObject();
		data.addProperty("fuseOffset", offset);
		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(player, ModType.TNT_TIME, data);
	}

	/**
	 * Resets the TNTTime fuse offset so that nothing is applied for every online Badlion user.
	 */
	public static void resetFuseOffset() {
		final JsonObject data = new JsonObject();
		data.addProperty("fuseOffset", 0);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(ModType.TNT_TIME, data);
	}

	/**
	 * Resets the TNTTime fuse offset so that nothing is applied for a specific Badlion user.
	 *
	 * @param player The player who will receive the counter
	 */
	public static void resetFuseOffset(UUID player) {
		final JsonObject data = new JsonObject();
		data.addProperty("fuseOffset", 0);

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendModData(player, ModType.TNT_TIME, data);
	}
}