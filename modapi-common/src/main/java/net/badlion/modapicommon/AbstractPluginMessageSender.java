package net.badlion.modapicommon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractPluginMessageSender {

	/**
	 * Sends a plugin message to every online Badlion player.
	 *
	 * @param mod  The name of the mod this data is for
	 * @param data The payload data
	 */
	public void sendModData(String mod, JsonElement data) {
		final JsonObject object = new JsonObject();

		object.addProperty("mod", mod);
		object.add("payload", data);

		this.sendPluginMessage(object.toString().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Sends a plugin message to a specific player, identified by the player's UUID.
	 *
	 * @param player UUID of the player
	 * @param mod    The name of the mod this data is for
	 * @param data   The payload data
	 */
	public void sendModData(UUID player, String mod, JsonElement data) {
		if (player == null) {
			return;
		}

		final JsonObject object = new JsonObject();

		object.addProperty("mod", mod);
		object.add("payload", data);

		this.sendPluginMessage(player, object.toString().getBytes(StandardCharsets.UTF_8));
	}

	public abstract void sendPluginMessage(byte[] data);

	public abstract void sendPluginMessage(UUID player, byte[] data);
}