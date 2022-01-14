package net.badlion.velocityapi.waypoints;

import net.badlion.modapicommon.utility.AbstractWaypoint;
import net.badlion.modapicommon.utility.AbstractWaypointManager;

import java.util.UUID;

public class UnsupportedWaypointManager extends AbstractWaypointManager {

	@Override
	public void addPlayerWaypoint(UUID player, AbstractWaypoint waypoint) {
		throw new UnsupportedOperationException("Waypoint mod hooks are not supported on velocity!");
	}

	@Override
	public void removePlayerWaypoint(UUID player, int id) {
		throw new UnsupportedOperationException("Waypoint mod hooks are not supported on velocity!");
	}
}
