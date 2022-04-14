package net.badlion.bukkitapi.waypoints;

import net.badlion.modapicommon.utility.AbstractWaypoint;
import net.badlion.modapicommon.utility.Location;

public class ConfigWaypoint extends AbstractWaypoint {

	public ConfigWaypoint() {
		super(new Location("badlionexampleworld", 0, 0, 0), "Example Waypoint", false);
	}

	public ConfigWaypoint(Location location, String name, boolean allowToAdd) {
		super(location, name, allowToAdd);
	}

	public ConfigWaypoint clone() {
		return new ConfigWaypoint(this.getLocation(), this.getName(), this.isAllowToAdd());
	}

	@Override
	public Class<? extends AbstractWaypoint> getType() {
		return ConfigWaypoint.class;
	}
}