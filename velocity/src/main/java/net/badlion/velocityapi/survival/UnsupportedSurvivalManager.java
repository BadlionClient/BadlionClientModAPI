package net.badlion.velocityapi.survival;

import net.badlion.modapicommon.survival.AbstractSurvivalManager;
import net.badlion.modapicommon.survival.SurvivalFeature;

import java.util.Set;

public class UnsupportedSurvivalManager extends AbstractSurvivalManager {
	@Override
	public void enableAllFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on velocity!");
	}

	@Override
	public void disableAllFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on velocity!");
	}

	@Override
	public void enableFeature(SurvivalFeature feature) {
		throw new UnsupportedOperationException("Survival API is not supported on velocity!");
	}

	@Override
	public void disableFeature(SurvivalFeature feature) {
		throw new UnsupportedOperationException("Survival API is not supported on velocity!");
	}

	@Override
	public Set<SurvivalFeature> getEnabledFeatures() {
		throw new UnsupportedOperationException("Survival API is not supported on velocity!");
	}
}
