package net.badlion.modapicommon.survival;

import net.badlion.modapicommon.AbstractBadlionApi;

import java.util.Set;

public class SurvivalApi {
	public static void enableAllFeatures() {
		AbstractBadlionApi.getInstance().getSurvivalManager().enableAllFeatures();
	}

	public static void disableAllFeatures() {
		AbstractBadlionApi.getInstance().getSurvivalManager().disableAllFeatures();
	}

	public static void enableFeature(SurvivalFeature feature) {
		AbstractBadlionApi.getInstance().getSurvivalManager().enableFeature(feature);
	}

	public static void disableFeature(SurvivalFeature feature) {
		AbstractBadlionApi.getInstance().getSurvivalManager().disableFeature(feature);
	}

	public static Set<SurvivalFeature> getEnabledFeatures() {
		return AbstractBadlionApi.getInstance().getSurvivalManager().getEnabledFeatures();
	}
}
