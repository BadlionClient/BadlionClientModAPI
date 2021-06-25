package net.badlion.blcmodapibukkit;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class Conf {
	private Map<String, DisallowedMods> modsDisallowed = new HashMap<String, DisallowedMods>();

	public Map<String, DisallowedMods> getModsDisallowed() {
		return this.modsDisallowed;
	}

	private static class DisallowedMods {
		private boolean disabled;
		private JsonObject extra_data;
		private JsonObject settings;
	}
}
