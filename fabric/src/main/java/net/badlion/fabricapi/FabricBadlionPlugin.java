package net.badlion.fabricapi;

import net.badlion.fabricapi.cosmetics.CosmeticManager;
import net.badlion.fabricapi.events.PlayerJoinEvent;
import net.badlion.fabricapi.survival.SurvivalManager;
import net.badlion.fabricapi.waypoints.WaypointsManager;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.Config;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

@Environment(EnvType.SERVER)
public class FabricBadlionPlugin extends AbstractBadlionApi implements DedicatedServerModInitializer {

    private static final Logger LOGGER = LogManager.getLogger(FabricBadlionPlugin.class);
    private Identifier identifier;
    private MinecraftServer server;

    private Config config;

    private File dataFolder;

    private FabricPluginMessageSender messageSender;

    @Override
    public void onInitializeServer() {
        this.identifier = new Identifier("badlion:modapi");
        this.dataFolder = new File("mods", "BadlionClientModAPI");
        if (!this.dataFolder.exists()) {
            if (!this.dataFolder.mkdir()) {
                LOGGER.error("Failed to create plugin directory");
                return;
            }
        }

        try {
            this.loadConfig(new File(this.dataFolder, "config.json"));
        } catch (IOException e) {
            LOGGER.error("Failed to loading Badlion plugin", e);
            return;
        }


        PlayerJoinEvent.EVENT.register(new PlayerListener(this));

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.messageSender = new FabricPluginMessageSender(this, server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            this.saveConfig(this.config, new File(this.dataFolder, "config.json"));
        });

        this.setWaypointManager(new WaypointsManager(this));
        this.setCosmeticManager(new CosmeticManager());
        this.setSurvivalManager(new SurvivalManager());

        this.getWaypointManager().loadWaypoints();
        LOGGER.info("Successfully loaded Badlion Mod Api");
    }

    @Override
    public void loadConfig(File file) throws IOException {
        try (Reader reader = new FileReader(file)) {
            this.config = AbstractBadlionApi.GSON_NON_PRETTY.fromJson(reader, Config.class);

        } catch (FileNotFoundException ex) {
            LOGGER.info("No Config Found: Saving default...");

            final Config config = new Config();
            this.saveConfig(config, new File(this.dataFolder, "config.json"));

            this.config = config;
        }
    }

    @Override
    public void saveConfig(Config config, File file) {
        try (FileWriter writer = new FileWriter(file)) {
            AbstractBadlionApi.GSON_PRETTY.toJson(config, writer);

        } catch (Exception ex) {
            LOGGER.warn("Failed to save config: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Config getBadlionConfig() {
        return this.config;
    }

    @Override
    public FabricPluginMessageSender getPluginMessageSender() {
        return this.messageSender;
    }

    @Override
    public CosmeticManager getCosmeticManager() {
        return (CosmeticManager) super.getCosmeticManager();
    }

    @Override
    public WaypointsManager getWaypointManager() {
        return (WaypointsManager) super.getWaypointManager();
    }

    public Identifier identifier() {
        return this.identifier;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public File dataFolder() {
        return dataFolder;
    }
}
