package net.badlion.modapicommon.survival;

import java.util.Set;

public abstract class AbstractSurvivalManager {
	public abstract void enableAllFeatures();

	public abstract void disableAllFeatures();

	public abstract void enableFeature(SurvivalFeature feature);

	public abstract void disableFeature(SurvivalFeature feature);

	public abstract Set<SurvivalFeature> getEnabledFeatures();
}
