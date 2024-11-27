package net.badlion.bukkitapi.listener;

import net.badlion.bukkitapi.AbstractBukkitBadlionPlugin;
import net.badlion.bukkitapi.survival.BreedingTracker;
import net.badlion.bukkitapi.timers.TimerApi;
import net.badlion.modapicommon.AbstractBadlionApi;
import org.bukkit.Bukkit;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.nio.charset.StandardCharsets;

public class PlayerListener implements Listener {

	private final AbstractBukkitBadlionPlugin plugin;
	private final BreedingTracker breedingTracker;

	public PlayerListener(AbstractBukkitBadlionPlugin plugin) {
		this.plugin = plugin;
		this.breedingTracker = new BreedingTracker(plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Handle disallowed mods
		byte[] message = AbstractBadlionApi.GSON_NON_PRETTY.toJson(this.plugin.getBadlionApi().getBadlionConfig()).getBytes();
		this.plugin.getMessageSender().sendPluginMessagePacket(player, "badlion:modapi", message);

		// Handle timer api
		this.plugin.getMessageSender().sendPluginMessagePacket(player, TimerApi.CHANNEL_NAME, "REGISTER|{}".getBytes(StandardCharsets.UTF_8));
		this.plugin.getMessageSender().sendPluginMessagePacket(player, TimerApi.CHANNEL_NAME, "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));

		this.plugin.getWaypointManager().onPlayerJoin(player);
		this.plugin.getCosmeticManager().onPlayerJoin(player);
		this.plugin.getSurvivalManager().sendData(player);

		Bukkit.getScheduler().runTaskLater(this.plugin, () -> this.plugin.getWaypointManager().sendWaypointsToClient(player, player.getWorld()), 40);
		this.breedingTracker.trackAll(event.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.plugin.getTimerApi().clearTimers(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();

		if (event.getFrom().getWorld() != null && event.getTo() != null && !event.getFrom().getWorld().equals(event.getTo().getWorld())) {
			this.plugin.getWaypointManager().sendWaypointsToClient(player, event.getTo().getWorld());
			this.plugin.getMessageSender().sendPluginMessagePacket(event.getPlayer(), TimerApi.CHANNEL_NAME, "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onRespawn(PlayerRespawnEvent event) {
		this.plugin.getWaypointManager().sendWaypointsToClient(event.getPlayer(), event.getRespawnLocation().getWorld());
		this.plugin.getMessageSender().sendPluginMessagePacket(event.getPlayer(), TimerApi.CHANNEL_NAME, "CHANGE_WORLD|{}".getBytes(StandardCharsets.UTF_8));
	}

	@EventHandler
	public void onJoinWorld(PlayerChangedWorldEvent event) {
		this.plugin.getSurvivalManager().sendData(event.getPlayer());
		this.breedingTracker.trackAll(event.getPlayer());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onBreed(EntityBreedEvent event) {
		if (!(event.getBreeder() instanceof Player)) {
			return;
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () ->
				this.breedingTracker.track((Player) event.getBreeder(), false, event.getMother(), event.getFather(), event.getEntity()), 1);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();

		if (entity instanceof Ageable && ((Ageable) entity).getAge() != 0) {
			this.breedingTracker.track(event.getPlayer(), true, entity);
		}
	}
}