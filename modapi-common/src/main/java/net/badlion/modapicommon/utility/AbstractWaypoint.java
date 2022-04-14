package net.badlion.modapicommon.utility;

public abstract class AbstractWaypoint {
	private final Location location;
	private final String name;
	private final boolean allowToAdd;
	private boolean sentBefore = false;

	/**
	 * Creates a new abstract waypoint instance.
	 *
	 * @param location   The location for this waypoint.
	 * @param name       The name for this waypoint, color codes allowed.
	 * @param allowToAdd Whether or not the user would be allowed to add this waypoint from the client (prompted with a chat message).
	 */
	public AbstractWaypoint(Location location, String name, boolean allowToAdd) {
		this.location = location;
		this.name = name;
		this.allowToAdd = allowToAdd;
	}

	/**
	 * Returns the location of this waypoint.
	 *
	 * @return The location of this waypoint
	 */
	public Location getLocation() {
		return this.location;
	}

	/**
	 * Returns the name of this waypoint, color codes allowed.
	 *
	 * @return The name of this waypoint
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns whether or not the user could add this waypoint from the client (prompted with a chat message).
	 *
	 * @return {@code true} if the user can add this waypoint through chat, {@code false} otherwise
	 */
	public boolean isAllowToAdd() {
		return this.allowToAdd;
	}

	/**
	 * Sets whether or not this waypoint has been sent before to the player.
	 *
	 * @param sentBefore If the waypoint has been sent before
	 */
	public void setSentBefore(boolean sentBefore) {
		this.sentBefore = sentBefore;
	}

	/**
	 * Whether or not this waypoint has been sent before to the player.
	 *
	 * @return {@code true} if the waypoint has been sent before, {@code false} otherwise
	 */
	public boolean isSentBefore() {
		return this.sentBefore;
	}

	/**
	 * The extending class of this {@link AbstractWaypoint}.
	 *
	 * @return The type of this class
	 */
	public abstract Class<? extends AbstractWaypoint> getType();
}
