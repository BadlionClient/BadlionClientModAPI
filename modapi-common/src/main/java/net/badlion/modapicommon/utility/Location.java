package net.badlion.modapicommon.utility;

/**
 * Represents a location for a {@link AbstractWaypoint}.
 */
public class Location {

	private final String world;
	private final double x;
	private final double y;
	private final double z;

	public Location(String world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getWorld() {
		return this.world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public static Location of(String world, double x, double y, double z) {
		return new Location(world, x, y, z);
	}
}