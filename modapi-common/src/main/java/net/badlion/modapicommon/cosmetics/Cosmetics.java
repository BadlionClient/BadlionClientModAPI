package net.badlion.modapicommon.cosmetics;

import net.badlion.modapicommon.AbstractBadlionApi;

import java.util.UUID;

/**
 * Class with API Hooks for our cosmetics.
 */
public class Cosmetics {
	/**
	 * Disables nametag cosmetics for a certain user.
	 * Could be used in a /nick implementation to remove partner / blc staff icons or promotional nametags.
	 * Custom insider nametags (like BLC staff or partner) will be replaced by the default insider nametag.
	 *
	 * @param uuid The player UUID
	 */
	public static void disableNametagCosmetics(UUID uuid) {
		AbstractBadlionApi.getInstance().getCosmeticManager().disableNametagCosmetics(uuid);
	}

	/**
	 * Re-enables nametag cosmetics for a certain user, previously disabled by {@link Cosmetics#disableNametagCosmetics(UUID)}.
	 *
	 * @param uuid The player UUID
	 */
	public static void enableNametagCosmetics(UUID uuid) {
		AbstractBadlionApi.getInstance().getCosmeticManager().enableNametagCosmetics(uuid);
	}
}
