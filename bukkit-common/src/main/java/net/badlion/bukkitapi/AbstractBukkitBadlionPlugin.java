package net.badlion.bukkitapi;

import net.badlion.bukkitapi.cosmetics.CosmeticManager;
import net.badlion.bukkitapi.listener.PlayerListener;
import net.badlion.bukkitapi.survival.AbstractBukkitSurvivalManager;
import net.badlion.bukkitapi.teamviewer.TeamViewerManager;
import net.badlion.bukkitapi.timers.TimerApi;
import net.badlion.bukkitapi.timers.TimerApiImpl;
import net.badlion.bukkitapi.waypoints.WaypointManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class AbstractBukkitBadlionPlugin extends JavaPlugin {
	private final BukkitBadlionApi badlionApi;
	private final TimerApiImpl timerApi;
	private final WaypointManager waypointManager;
	private final CosmeticManager cosmeticManager;
	private AbstractBukkitPluginMessageSender messageSender;
	private AbstractBukkitSurvivalManager survivalManager;

	public AbstractBukkitBadlionPlugin() {
		this.badlionApi = new BukkitBadlionApi(this);
		this.timerApi = new TimerApiImpl(this);
		this.waypointManager = new WaypointManager(this);
		this.badlionApi.setWaypointManager(this.waypointManager);
		this.cosmeticManager = new CosmeticManager();
		this.badlionApi.setCosmeticManager(this.cosmeticManager);
		this.badlionApi.setTeamViewerManager(new TeamViewerManager(this));
	}

	protected void setMessageSender(AbstractBukkitPluginMessageSender messageSender) {
		this.messageSender = messageSender;
	}

	protected void setSurvivalManager(AbstractBukkitSurvivalManager survivalManager) {
		this.survivalManager = survivalManager;
		this.badlionApi.setSurvivalManager(survivalManager);
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
			this.survivalManager.loadConfig();

			// Register channel
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:mods");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "badlion:modapi");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, TimerApi.CHANNEL_NAME);

			// Only register the listener if the config loads successfully
			this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
			this.getServer().getPluginManager().registerEvents(this.waypointManager, this);
			this.getServer().getPluginManager().registerEvents(this.cosmeticManager, this);

			this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
				@Override
				public void run() {
					AbstractBukkitBadlionPlugin.this.getTimerApi().tickTimers();
				}
			}, 1L, 1L);

			this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
				@Override
				public void run() {
					AbstractBukkitBadlionPlugin.this.getTimerApi().syncTimers();
				}
			}, 60L, 60L);

			this.getLogger().log(Level.INFO, "Successfully setup BadlionClientModAPI plugin.");

		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE, "Error with config for BadlionClientModAPI plugin.");
			e.printStackTrace();
		}
	}

	public BukkitBadlionApi getBadlionApi() {
		return this.badlionApi;
	}

	public AbstractBukkitPluginMessageSender getMessageSender() {
		return this.messageSender;
	}

	public TimerApiImpl getTimerApi() {
		return this.timerApi;
	}

	public WaypointManager getWaypointManager() {
		return this.waypointManager;
	}

	public CosmeticManager getCosmeticManager() {
		return this.cosmeticManager;
	}

	public AbstractBukkitSurvivalManager getSurvivalManager() {
		return this.survivalManager;
	}
}