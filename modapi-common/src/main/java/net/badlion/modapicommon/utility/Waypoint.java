package net.badlion.modapicommon.utility;

public class Waypoint extends AbstractWaypoint {
	private final int id;

	public Waypoint(int id, Location location, String name, boolean allowToAdd) {
		super(location, name, allowToAdd);
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public Class<? extends AbstractWaypoint> getType() {
		return Waypoint.class;
	}
}
