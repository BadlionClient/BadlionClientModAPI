package net.badlion.modapicommon;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("FieldCanBeLocal")
public class Config {

	private int clicksPerSecondLimitRight = 0;
	private int clicksPerSecondLimit = 0;

	private final Map<String, DisallowedMods> modsDisallowed = new HashMap<>();

	public Map<String, DisallowedMods> getModsDisallowed() {
		return this.modsDisallowed;
	}

	public void setClicksPerSecondLimitRight(int clicksPerSecondLimitRight) {
		this.clicksPerSecondLimitRight = clicksPerSecondLimitRight;
	}

	public void setClicksPerSecondLimit(int clicksPerSecondLimit) {
		this.clicksPerSecondLimit = clicksPerSecondLimit;
	}

	public static class DisallowedMods {
		private boolean disabled;
		private JsonObject extra_data;
		private JsonObject settings;
	}
}