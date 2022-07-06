package net.badlion.modapicommon.utility;

import java.util.UUID;

public class TeamMemberLocation {

	private final UUID uuid;
	private final int color;
	private final double x;
	private final double y;
	private final double z;

	public TeamMemberLocation(UUID uuid, int color, double x, double y, double z) {
		this.uuid = uuid;
		this.color = color;
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
