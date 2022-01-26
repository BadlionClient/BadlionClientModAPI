package net.badlion.modapicommon.utility;

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
}
