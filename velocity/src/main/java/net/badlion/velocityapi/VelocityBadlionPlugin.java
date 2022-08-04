package net.badlion.velocityapi;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.badlion.velocityapi.cosmetics.UnsupportedCosmeticManager;
import net.badlion.velocityapi.listener.PlayerListener;
import net.badlion.velocityapi.survival.UnsupportedSurvivalManager;
import net.badlion.velocityapi.teammarker.UnsupportedTeamMarkerManager;
import net.badlion.velocityapi.waypoints.UnsupportedWaypointManager;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;

@Plugin(
	id = "badlion-modapi",
	name = "Badlion Client Mod Api",
	version = "2.0",
	authors = {"Badlion"}
)
public class VelocityBadlionPlugin {
	private final ProxyServer proxyServer;
	private final Logger logger;
	private final VelocityBadlionApi badlionApi;
	private final VelocityPluginMessageSender messageSender;
	private final File dataFolder;

	@Inject
	public VelocityBadlionPlugin(ProxyServer proxyServer, Logger logger) {
		this.proxyServer = proxyServer;
		this.logger = logger;
		this.badlionApi = new VelocityBadlionApi(this);
		this.messageSender = new VelocityPluginMessageSender(this);
		this.dataFolder = new File("plugins", "BadlionClientModAPI");
		this.badlionApi.setWaypointManager(new UnsupportedWaypointManager());
		this.badlionApi.setCosmeticManager(new UnsupportedCosmeticManager());
		this.badlionApi.setSurvivalManager(new UnsupportedSurvivalManager());
		this.badlionApi.setTeamMarkerManager(new UnsupportedTeamMarkerManager());
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				this.getLogger().error("Failed to create plugin directory.");
			}
		}

		try {
			this.badlionApi.loadConfig(new File(this.getDataFolder(), "config.json"));

			// Only register the listener if the config loads successfully
			this.getProxy().getEventManager().register(this, new PlayerListener(this));

			this.getLogger().info("Successfully setup BadlionClientModAPI plugin.");
		} catch (IOException ex) {
			this.getLogger().error("Error with config for BadlionClientModAPI plugin: " + ex.getMessage(), ex);
		}
	}

	public ProxyServer getProxy() {
		return this.proxyServer;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public VelocityBadlionApi getBadlionApi() {
		return this.badlionApi;
	}

	public VelocityPluginMessageSender getMessageSender() {
		return this.messageSender;
	}

	public File getDataFolder() {
		return this.dataFolder;
	}
}