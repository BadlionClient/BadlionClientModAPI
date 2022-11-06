package net.badlion.fabricapi.waypoints;

import com.google.common.collect.Queues;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.badlion.fabricapi.FabricBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.mods.ModType;
import net.badlion.modapicommon.utility.AbstractWaypoint;
import net.badlion.modapicommon.utility.AbstractWaypointManager;
import net.badlion.modapicommon.utility.Waypoint;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WaypointsManager extends AbstractWaypointManager {
    private static final Logger LOGGER = LogManager.getLogger(WaypointsManager.class);
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final FabricBadlionPlugin plugin;
    private final ConcurrentHashMap<UUID, List<AbstractWaypoint>> playerWaypoints = new ConcurrentHashMap<>();
    private final Queue<UUID> scheduledUpdates = Queues.newConcurrentLinkedQueue();
    private List<ConfigWaypoint> configWaypoints = new ArrayList<>();

    public WaypointsManager(FabricBadlionPlugin fabricBadlionPlugin) {
        this.plugin = fabricBadlionPlugin;
    }

    public void loadWaypoints() {
        executorService.scheduleWithFixedDelay(() -> {
            if (this.scheduledUpdates.isEmpty()) {
                return;
            }

            for (int i = 0; i < 25; i++) {
                final UUID uuid = this.scheduledUpdates.poll();

                if (uuid == null) {
                    continue;
                }

                ServerPlayerEntity player = this.plugin.getServer().getPlayerManager().getPlayer(uuid);

                if (player != null) {
                    this.sendWaypointsToClient(player, player.getWorld());
                }
            }
        }, 0, 1, TimeUnit.SECONDS);


        final Path waypointsConfig = this.plugin.dataFolder().toPath().resolve("waypoints.json");

        if (Files.exists(waypointsConfig)) {
            try (BufferedReader reader = Files.newBufferedReader(waypointsConfig, StandardCharsets.UTF_8)) {
                this.configWaypoints = AbstractBadlionApi.GSON_PRETTY.fromJson(reader, new TypeToken<List<ConfigWaypoint>>() {
                }.getType());

            } catch (IOException e) {
                LOGGER.error("Failed to read waypoints from waypoints config", e);
            }

            if (this.configWaypoints.size() > 0) {
                LOGGER.info("Loaded {} waypoints from config", this.configWaypoints.size());
            }

        } else {
            LOGGER.info("Waypoints config does not exist, creating one now. Read our documentation for more information about this!");

            this.configWaypoints.add(new ConfigWaypoint());

            try (BufferedWriter writer = Files.newBufferedWriter(waypointsConfig)) {
                AbstractBadlionApi.GSON_PRETTY.toJson(this.configWaypoints, new TypeToken<List<ConfigWaypoint>>() {
                }.getType(), writer);

            } catch (IOException e) {
                LOGGER.error("Failed to create waypoints config", e);
            }
        }
    }

    public List<AbstractWaypoint> getWaypoints(ServerPlayerEntity player, ServerWorld world) {
        ServerWorldProperties levelProperties = (ServerWorldProperties) world.getLevelProperties();
        String levelName = levelProperties.getLevelName();
        return this.playerWaypoints.computeIfAbsent(player.getUuid(), k -> new ArrayList<>())
            .stream()
            .filter(Objects::nonNull)
            .filter(waypoint -> waypoint.getLocation().getWorld() != null && waypoint.getLocation().getWorld().equals(levelName))
            .collect(Collectors.toList());
    }

    public void sendWaypointsToClient(ServerPlayerEntity player, ServerWorld world) {
        this.resetWaypoints(player.getUuid());

        final List<AbstractWaypoint> waypoints = this.getWaypoints(player, world);
        final JsonArray jsonWaypoints = new JsonArray();

        for (AbstractWaypoint waypoint : waypoints) {
            jsonWaypoints.add(AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(waypoint, waypoint.getType()));
        }

        JsonObject data = new JsonObject();
        data.addProperty("action", "add");
        data.add("waypoints", jsonWaypoints);

        this.plugin.getPluginMessageSender().sendModData(player.getUuid(), ModType.WAYPOINTS, data);

        waypoints.forEach(waypoint -> {
            if (!waypoint.isSentBefore()) {
                waypoint.setSentBefore(true);
            }
        });
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (!this.configWaypoints.isEmpty()) {
            // Add all config waypoints to player
            this.playerWaypoints.computeIfAbsent(player.getUuid(), k -> new ArrayList<>()).addAll(
                this.configWaypoints.stream().map(ConfigWaypoint::clone).toList()
            );

            this.scheduledUpdates.add(player.getUuid());
        }
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        this.playerWaypoints.remove(player.getUuid());
        this.scheduledUpdates.remove(player.getUuid());
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
