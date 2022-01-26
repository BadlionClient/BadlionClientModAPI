package net.badlion.bungeeapi.cosmetics;

import net.badlion.modapicommon.utility.AbstractCosmeticManager;

import java.util.UUID;

public class UnsupportedCosmeticManager extends AbstractCosmeticManager {
	@Override
	public void disableNametagCosmetics(UUID uuid) {
		throw new UnsupportedOperationException("Cosmetic settings hooks are not supported on bungee!");
	}

	@Override
	public void enableNametagCosmetics(UUID uuid) {
		throw new UnsupportedOperationException("Cosmetic settings hooks are not supported on bungee!");
	}
}
