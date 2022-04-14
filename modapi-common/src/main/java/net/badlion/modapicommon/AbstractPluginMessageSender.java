package net.badlion.modapicommon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.badlion.modapicommon.mods.ModType;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractPluginMessageSender {

	/**
	 * Sends a plugin message to every online Badlion player directed towards a certain Badlion mod.
	 *
	 * @param mod  The name of the mod this data is for
	 * @param data The payload data
	 */
	public void sendModData(ModType mod, JsonElement data) {
		final JsonObject object = new JsonObject();

		object.addProperty("mod", mod.getType());
		object.add("payload", data);

		this.sendPluginMessage(object.toString().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Sends a plugin message to a specific player, identified by the player's UUID, directed towards a certain Badlion mod.
	 *
	 * @param player UUID of the player
	 * @param mod    The name of the mod this data is for
	 * @param data   The payload data
	 */
	public void sendModData(UUID player, ModType mod, JsonElement data) {
		if (player == null) {
			return;
		}

		final JsonObject object = new JsonObject();

		object.addProperty("mod", mod.getType());
		object.add("payload", data);

		this.sendPluginMessage(player, object.toString().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Used to send data unrelated to mods to the client.
	 *
	 * @param type Type of message to send
	 * @param data Json data to send
	 */
	public void sendData(String type, JsonElement data) {
		final JsonObject object = new JsonObject();

		object.addProperty("type", type);
		object.add("payload", data);

		this.sendPluginMessage(object.toString().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Used to send data unrelated to mods to the client.
	 *
	 * @param player Player who will receive the data
	 * @param type   Type of message to send
	 * @param data   Json data to send
	 */
	public void sendData(UUID player, String type, JsonElement data) {
		if (player == null) {
			return;
		}

		final JsonObject object = new JsonObject();

		object.addProperty("type", type);
		object.add("payload", data);

		this.sendPluginMessage(player, object.toString().getBytes(StandardCharsets.UTF_8));
	}

	public abstract void sendPluginMessage(byte[] data);

	public abstract void sendPluginMessage(UUID player, byte[] data);
}