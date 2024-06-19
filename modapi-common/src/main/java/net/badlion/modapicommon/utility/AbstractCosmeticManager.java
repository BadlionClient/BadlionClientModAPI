package net.badlion.modapicommon.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.badlion.modapicommon.cosmetics.Cosmetics;

import java.util.UUID;

/**
 * Handles hooks into the Badlion Cosmetics options for players.
 */
public abstract class AbstractCosmeticManager {

	/**
	 * Disables nametag cosmetics for a certain user.
	 * Could be used in a /nick implementation to remove partner / blc staff icons or promotional nametags.
	 *
	 * @param uuid The player UUID
	 */
	public abstract void disableNametagCosmetics(UUID uuid);

	/**
	 * Re-enables nametag cosmetics for a certain user, previously disabled by {@link Cosmetics#disableNametagCosmetics(UUID)}.
	 *
	 * @param uuid The player UUID
	 */
	public abstract void enableNametagCosmetics(UUID uuid);

	protected JsonObject getDisabledCosmeticsData(UUID uuid, boolean disabled, String... cosmeticTypes) {
		final JsonObject data = new JsonObject();
		final JsonArray array = new JsonArray();

		for (String cosmeticType : cosmeticTypes) {
			array.add(cosmeticType);
		}

		data.addProperty("type", "disable_cosmetics");
		data.add("cosmeticTypes", array);
		data.addProperty("disable", disabled);
		data.addProperty("uuid", uuid.toString());

		return data;
	}
}
