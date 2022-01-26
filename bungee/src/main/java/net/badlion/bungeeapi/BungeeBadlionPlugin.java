package net.badlion.bungeeapi;

import net.badlion.bungeeapi.cosmetics.UnsupportedCosmeticManager;
import net.badlion.bungeeapi.listener.PlayerListener;
import net.badlion.bungeeapi.waypoints.UnsupportedWaypointManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BungeeBadlionPlugin extends Plugin {
	private final BungeeBadlionApi badlionApi;
	private final BungeePluginMessageSender messageSender;

	public BungeeBadlionPlugin() {
		super();
		this.badlionApi = new BungeeBadlionApi(this);
		this.messageSender = new BungeePluginMessageSender(this);
		this.badlionApi.setWaypointManager(new UnsupportedWaypointManager());
		this.badlionApi.setCosmeticManager(new UnsupportedCosmeticManager());
	}

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				this.getLogger().log(Level.SEVERE, "Failed to create plugin directory.");
			}
		}

		try {
			this.badlionApi.loadConfig(new File(this.getDataFolder(), "config.json"));

			// Only register the listener if the config loads successfully
			this.getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

			this.getLogger().log(Level.INFO, "Successfully setup BadlionClientModAPI plugin.");
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error with config for BadlionClientModAPI plugin.");
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		this.badlionApi.saveConfig(this.badlionApi.getBadlionConfig(), new File(this.getDataFolder(), "config.json"));
	}

	public BungeeBadlionApi getBadlionApi() {
		return this.badlionApi;
	}

	public BungeePluginMessageSender getMessageSender() {
		return this.messageSender;
	}
}