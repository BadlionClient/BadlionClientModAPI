package net.badlion.bukkitapi.survival;

import net.badlion.modapicommon.survival.SurvivalFeature;
import org.bukkit.entity.Player;

import java.util.Set;

public class UnsupportedSurvivalManager extends AbstractBukkitSurvivalManager {
	@Override
	public void loadConfig() {

	}

	@Override
	public void sendData(Player player) {

	}

	@Override
	public void enableAllFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on MC prior to 1.17!");
	}

	@Override
	public void disableAllFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on MC prior to 1.17!");
	}

	@Override
	public void enableFeature(SurvivalFeature feature) {
		throw new UnsupportedOperationException("Survival API is not supported on MC prior to 1.17!");
	}

	@Override
	public void disableFeature(SurvivalFeature feature) {
		throw new UnsupportedOperationException("Survival API is not supported on MC prior to 1.17!");
	}

	@Override
	public Set<SurvivalFeature> getEnabledFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on MC prior to 1.17!");
	}
}
