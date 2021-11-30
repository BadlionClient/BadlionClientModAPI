package net.badlion.blcmodapibukkit;

import net.badlion.blcmodapibukkit.listener.PlayerListener;
import net.badlion.blcmodapibukkit.timers.TimerApi;
import net.badlion.blcmodapibukkit.timers.TimerApiImpl;
import net.badlion.blcmodapibukkit.waypoints.WaypointManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BlcModApiBukkit extends JavaPlugin {
	private final BukkitBadlionApi badlionApi;
	private final BukkitPluginMessageSender messageSender;
	private final TimerApiImpl timerApi;
	private final WaypointManager waypointManager;

	public BlcModApiBukkit() {
		this.badlionApi = new BukkitBadlionApi(this);
		this.messageSender = new BukkitPluginMessageSender(this);
		this.timerApi = new TimerApiImpl(this);
		this.waypointManager = new WaypointManager(this);
		this.badlionApi.setWaypointManager(this.waypointManager);
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

			this.waypointManager.loadWaypoints();

			// Register channel
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:mods");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:modapi");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, TimerApi.CHANNEL_NAME);

			// Only register the listener if the config loads successfully
			this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
			this.getServer().getPluginManager().registerEvents(this.waypointManager, this);

			this.getLogger().log(Level.INFO, "Successfully setup BadlionClientModAPI plugin.");

			this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
				@Override
				public void run() {
					BlcModApiBukkit.this.getTimerApi().tickTimers();
				}
			}, 1L, 1L);

			this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
				@Override
				public void run() {
					BlcModApiBukkit.this.getTimerApi().syncTimers();
				}
			}, 60L, 60L);

		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error with config for BadlionClientModAPI plugin.");
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		this.badlionApi.saveConfig(this.badlionApi.getBadlionConfig(), new File(this.getDataFolder(), "config.json"));
	}

	public BukkitBadlionApi getBadlionApi() {
		return this.badlionApi;
	}

	public BukkitPluginMessageSender getMessageSender() {
		return this.messageSender;
	}

	public TimerApiImpl getTimerApi() {
		return this.timerApi;
	}

	public WaypointManager getWaypointManager() {
		return this.waypointManager;
	}
}
