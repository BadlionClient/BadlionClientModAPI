package net.badlion.bukkitapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.AbstractPluginMessageSender;
import net.badlion.modapicommon.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public class BukkitBadlionApi extends AbstractBadlionApi {
	private final AbstractBukkitBadlionPlugin apiBukkit;
	private Config config;

	public BukkitBadlionApi(AbstractBukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	@Override
	public void loadConfig(File file) throws IOException {
		try (Reader reader = new FileReader(file)) {
			this.config = AbstractBadlionApi.GSON_NON_PRETTY.fromJson(reader, Config.class);

		} catch (FileNotFoundException ex) {
			this.apiBukkit.getLogger().log(Level.INFO, "No Config Found: Saving default...");

			final Config config = new Config();
			this.saveConfig(config, file);

			this.config = config;
		}

		try {
			this.findOtherConfigs();

		} catch (Exception e) {
			this.apiBukkit.getLogger().log(Level.WARNING, "Problem occurred trying to read old config");
			e.printStackTrace();
		}
	}

	private void findOtherConfigs() throws IOException {
		final Path cpsConfig = this.apiBukkit.getServer().getWorldContainer().toPath().resolve("plugins/BadlionClientCPSAPI/config.json");

		if (Files.exists(cpsConfig)) {
			this.apiBukkit.getLogger().log(Level.INFO, "Found config file from old Badlion Client CPS API, attempting to import config...");

			JsonObject oldConfig = new JsonParser().parse(new String(Files.readAllBytes(cpsConfig))).getAsJsonObject();

			this.config.setClicksPerSecondLimitRight(oldConfig.get("clicksPerSecondLimitRight").getAsInt());
			this.config.setClicksPerSecondLimit(oldConfig.get("clicksPerSecondLimit").getAsInt());

			this.saveConfig(this.config, new File(this.apiBukkit.getDataFolder(), "config.json"));

			this.apiBukkit.getLogger().log(Level.WARNING, "Imported config from cps api to new config, deleting old config...");
			this.apiBukkit.getLogger().log(Level.WARNING, "Since this version of the Badlion Mod API, the CPS API plugin is no longer necessary! Please delete it.");

			Files.deleteIfExists(cpsConfig);
		}
	}

	@Override
	public void saveConfig(Config config, File file) {
		try (FileWriter writer = new FileWriter(file)) {
			AbstractBadlionApi.GSON_PRETTY.toJson(config, writer);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public Config getBadlionConfig() {
		return this.config;
	}

	@Override
	public AbstractPluginMessageSender getPluginMessageSender() {
		return this.apiBukkit.getMessageSender();
	}
}
