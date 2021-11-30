package net.badlion.modapicommon.utility;

/**
 * Represents a location for a {@link AbstractWaypoint}.
 */
public class Location {

	private final String world;
	private final int x;
	private final int y;
	private final int z;

	public Location(String world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String getWorld() {
		return this.world;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public static Location of(String world, int x, int y, int z) {
		return new Location(world, x, y, z);
	}
}