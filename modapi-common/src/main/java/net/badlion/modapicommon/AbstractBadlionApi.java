package net.badlion.modapicommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.modapicommon.survival.AbstractSurvivalManager;
import net.badlion.modapicommon.utility.AbstractCosmeticManager;
import net.badlion.modapicommon.utility.AbstractTeamMarkerManager;
import net.badlion.modapicommon.utility.AbstractWaypointManager;

import java.io.File;
import java.io.IOException;

public abstract class AbstractBadlionApi {
	public static final Gson GSON_NON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
	public static final Gson GSON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create();

	private static AbstractBadlionApi instance;

	private AbstractWaypointManager waypointManager;
	private AbstractCosmeticManager cosmeticManager;
	private AbstractSurvivalManager survivalManager;
	private AbstractTeamMarkerManager teamMarkerManager;

	protected AbstractBadlionApi() {
		AbstractBadlionApi.instance = this;
	}

	/**
	 * Loads the necessary Badlion API configurations.
	 *
	 * @param file Config file to load
	 * @throws IOException Exception if anything went wrong while loading the config
	 */
	public abstract void loadConfig(File file) throws IOException;

	/**
	 * Saves the necessary Badlion API configurations.
	 *
	 * @param config Configuration to save
	 * @param file   Output file
	 */
	public abstract void saveConfig(Config config, File file);

	/**
	 * Returns the Badlion {@link Config} object.
	 *
	 * @return The config instance
	 */
	public abstract Config getBadlionConfig();

	/**
	 * Returns the {@link AbstractPluginMessageSender} responsible for sending plugin messages.
	 *
	 * @return The plugin message sender instance
	 */
	public abstract AbstractPluginMessageSender getPluginMessageSender();

	/**
	 * Sets the implementation for the {@link AbstractWaypointManager} class.
	 *
	 * @param waypointManager The waypoint manager instance
	 */
	public void setWaypointManager(AbstractWaypointManager waypointManager) {
		this.waypointManager = waypointManager;
	}

	/**
	 * Returns the implementation for the {@link AbstractWaypointManager} class.
	 *
	 * @return The waypoint manager instance
	 */
	public AbstractWaypointManager getWaypointManager() {
		return this.waypointManager;
	}

	/**
	 * Sets the implementation for the {@link AbstractCosmeticManager} class.
	 *
	 * @param cosmeticManager The cosmetic manager instance
	 */
	public void setCosmeticManager(AbstractCosmeticManager cosmeticManager) {
		this.cosmeticManager = cosmeticManager;
	}

	/**
	 * Returns the implementation for the {@link AbstractCosmeticManager} class.
	 *
	 * @return The cosmetic manager instance
	 */
	public AbstractCosmeticManager getCosmeticManager() {
		return this.cosmeticManager;
	}

	/**
	 * Sets the implementation for the {@link AbstractSurvivalManager} class.
	 *
	 * @param survivalManager The survival manager instance
	 */
	public void setSurvivalManager(AbstractSurvivalManager survivalManager) {
		this.survivalManager = survivalManager;
	}

	/**
	 * Returns the implementation for the {@link AbstractSurvivalManager} class.
	 *
	 * @return The survival manager instance
	 */
	public AbstractSurvivalManager getSurvivalManager() {
		return this.survivalManager;
	}

	/**
	 * Sets the implementation for the {@link AbstractTeamMarkerManager} class.
	 *
	 * @param teamMarkerManager The team marker manager instance
	 */
	public void setTeamMarkerManager(AbstractTeamMarkerManager teamMarkerManager) {
		this.teamMarkerManager = teamMarkerManager;
	}

	/**
	 * Returns the implementation for the {@link AbstractTeamMarkerManager} class.
	 *
	 * @return The team marker manager instance
	 */
	public AbstractTeamMarkerManager getTeamMarkerManager() {
		return this.teamMarkerManager;
	}

	/**
	 * Returns the Badlion Api instance.
	 *
	 * @return The current Badlion Api instance
	 */
	public static AbstractBadlionApi getInstance() {
		return AbstractBadlionApi.instance;
	}
}