package net.badlion.bukkitapi.waypoints;

import com.google.common.collect.Queues;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.badlion.bukkitapi.BukkitBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.AbstractWaypoint;
import net.badlion.modapicommon.utility.AbstractWaypointManager;
import net.badlion.modapicommon.utility.Waypoint;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class WaypointManager extends AbstractWaypointManager implements Listener {

	private final BukkitBadlionPlugin apiBukkit;
	private final ConcurrentHashMap<UUID, List<AbstractWaypoint>> playerWaypoints = new ConcurrentHashMap<>();
	private final Queue<UUID> scheduledUpdates = Queues.newConcurrentLinkedQueue();

	private List<ConfigWaypoint> configWaypoints = new ArrayList<>();

	public WaypointManager(BukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	public void loadWaypoints() {
		this.apiBukkit.getServer().getScheduler().runTaskTimerAsynchronously(this.apiBukkit, () -> {
			if (this.scheduledUpdates.isEmpty()) {
				return;
			}

			for (int i = 0; i < 25; i++) {
				final UUID uuid = this.scheduledUpdates.poll();

				if (uuid == null) {
					continue;
				}

				final Player player = Bukkit.getPlayer(uuid);

				if (player != null) {
					this.sendWaypointsToClient(player, player.getWorld());
				}
			}
		}, 20, 20);

		final Path waypointsConfig = this.apiBukkit.getDataFolder().toPath().resolve("waypoints.json");

		if (Files.exists(waypointsConfig)) {
			try (BufferedReader reader = Files.newBufferedReader(waypointsConfig, StandardCharsets.UTF_8)) {
				this.configWaypoints = AbstractBadlionApi.GSON_PRETTY.fromJson(reader, new TypeToken<List<ConfigWaypoint>>() {}.getType());

			} catch (IOException e) {
				this.apiBukkit.getLogger().log(Level.WARNING, "Failed to read waypoints from waypoints config");
				e.printStackTrace();
			}

			if (this.configWaypoints.size() > 0) {
				this.apiBukkit.getLogger().log(Level.INFO, "Loaded " + this.configWaypoints.size() + " waypoints from config!");
			}

		} else {
			this.apiBukkit.getLogger().log(Level.INFO, "Waypoints config does not exist, creating one now. Read our documentation for more information about this!");
			this.configWaypoints.add(new ConfigWaypoint());

			try (BufferedWriter writer = Files.newBufferedWriter(waypointsConfig)) {
				AbstractBadlionApi.GSON_PRETTY.toJson(this.configWaypoints, new TypeToken<List<ConfigWaypoint>>() {}.getType(), writer);

			} catch (IOException e) {
				this.apiBukkit.getLogger().log(Level.WARNING, "Failed to create waypoints config");
				e.printStackTrace();
			}
		}
	}

	public List<AbstractWaypoint> getWaypoints(Player player, World world) {
		return this.playerWaypoints.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
			.stream()
			.filter(Objects::nonNull)
			.filter(waypoint -> waypoint.getLocation().getWorld() != null && waypoint.getLocation().getWorld().equals(world.getName()))
			.collect(Collectors.toList());
	}

	public void sendWaypointsToClient(Player player, World world) {
		this.resetWaypoints(player.getUniqueId());

		final List<AbstractWaypoint> waypoints = this.getWaypoints(player, world);
		final JsonArray jsonWaypoints = new JsonArray();

		for (AbstractWaypoint waypoint : waypoints) {
			jsonWaypoints.add(AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(waypoint, waypoint.getType()));
		}

		JsonObject data = new JsonObject();
		data.addProperty("action", "add");
		data.add("waypoints", jsonWaypoints);

		this.apiBukkit.getMessageSender().sendModData(player.getUniqueId(), "waypoints", data);

		waypoints.forEach(waypoint -> {
			if (!waypoint.isSentBefore()) {
				waypoint.setSentBefore(true);
			}
		});
	}

	public void onPlayerJoin(Player player) {
		if (!this.configWaypoints.isEmpty()) {
			// Add all config waypoints to player
			this.playerWaypoints.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).addAll(
				this.configWaypoints.stream().map(ConfigWaypoint::clone).collect(Collectors.toList())
			);
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.playerWaypoints.remove(event.getPlayer().getUniqueId());
		this.scheduledUpdates.remove(event.getPlayer().getUniqueId());
	}

	@Override
	public void addPlayerWaypoint(UUID player, AbstractWaypoint waypoint) {
		this.playerWaypoints.computeIfAbsent(player, k -> new ArrayList<>()).add(waypoint);

		if (!this.scheduledUpdates.contains(player)) {
			this.scheduledUpdates.add(player);
		}
	}

	@Override
	public void removePlayerWaypoint(UUID player, int id) {
		this.playerWaypoints.computeIfAbsent(player, k -> new ArrayList<>()).removeIf(waypoint -> {
			if (waypoint instanceof Waypoint) {
				boolean removed = ((Waypoint) waypoint).getId() == id;

				if (removed) {
					if (!this.scheduledUpdates.contains(player)) {
						this.scheduledUpdates.add(player);
					}
				}

				return removed;
			}

			return false;
		});
	}
}