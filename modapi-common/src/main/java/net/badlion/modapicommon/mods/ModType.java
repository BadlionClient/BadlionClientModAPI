package net.badlion.modapicommon.mods;

public enum ModType {

	HEIGHT_OVERLAY("heightOverlay"),
	WAYPOINTS("waypoints"),
	TNT_TIME("tntTime"),
	NOTIFICATION("notification"),
	SURVIVAL("survival"),
	TEAM_VIEWER("teamViewer");

	private final String type;

	ModType(String name) {
		this.type = name;
	}

	public String getType() {
		return this.type;
	}
}
