package net.badlion.modapicommon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.modapicommon.utility.AbstractWaypointManager;

import java.io.File;
import java.io.IOException;

public abstract class AbstractBadlionApi {
	public static final Gson GSON_NON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
	public static final Gson GSON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create();

	private static AbstractBadlionApi instance;

	private AbstractWaypointManager waypointManager;

	public AbstractBadlionApi() {
		AbstractBadlionApi.instance = this;
	}

	/**
	 * Loads the necessary Badlion API configurations.
	 */
	public abstract void loadConfig(File file) throws IOException;

	/**
	 * Saves the necessary Badlion API configurations.
	 */
	public abstract void saveConfig(Config config, File file);

	/**
	 * Returns the Badlion {@link Config} object.
	 */
	public abstract Config getBadlionConfig();

	/**
	 * Returns the {@link AbstractPluginMessageSender} responsible for sending plugin messages.
	 */
	public abstract AbstractPluginMessageSender getPluginMessageSender();

	/**
	 * Sets the implementation for the {@link AbstractWaypointManager} class.
	 */
	public void setWaypointManager(AbstractWaypointManager waypointManager) {
		this.waypointManager = waypointManager;
	}

	/**
	 * Returns the implementation for the {@link AbstractWaypointManager} class.
	 */
	public AbstractWaypointManager getWaypointManager() {
		return this.waypointManager;
	}

	public static AbstractBadlionApi getInstance() {
		return AbstractBadlionApi.instance;
	}
}