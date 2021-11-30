package net.badlion.blcmodapibungee;

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

public class BungeeBadlionApi extends AbstractBadlionApi {
	private final BlcModApiBungee apiBungee;
	private Config config;

	public BungeeBadlionApi(BlcModApiBungee apiBungee) {
		super();
		this.apiBungee = apiBungee;
	}

	@Override
	public void loadConfig(File file) throws IOException {
		try (Reader reader = new FileReader(file)) {
			this.config = AbstractBadlionApi.GSON_NON_PRETTY.fromJson(reader, Config.class);

		} catch (FileNotFoundException ex) {
			this.apiBungee.getLogger().log(Level.INFO, "No Config Found: Saving default...");

			final Config config = new Config();
			this.saveConfig(config, new File(this.apiBungee.getDataFolder(), "config.json"));

			this.config = config;
		}

		try {
			this.findOtherConfigs();

		} catch (Exception e) {
			this.apiBungee.getLogger().log(Level.WARNING, "Problem occurred trying to read old config");
			e.printStackTrace();
		}
	}

	private void findOtherConfigs() throws IOException {
		final Path cpsConfig = this.apiBungee.getProxy().getPluginsFolder().toPath().resolve("BadlionClientCPSAPI/config.json");

		if (Files.exists(cpsConfig)) {
			this.apiBungee.getLogger().log(Level.INFO, "Found config file from old Badlion Client CPS API, attempting to import config...");

			JsonObject oldConfig = new JsonParser().parse(new String(Files.readAllBytes(cpsConfig))).getAsJsonObject();

			this.config.setClicksPerSecondLimitRight(oldConfig.get("clicksPerSecondLimitRight").getAsInt());
			this.config.setClicksPerSecondLimit(oldConfig.get("clicksPerSecondLimit").getAsInt());

			this.saveConfig(this.config, new File(this.apiBungee.getDataFolder(), "config.json"));

			this.apiBungee.getLogger().log(Level.WARNING, "Imported config from cps api to new config, deleting old config...");
			this.apiBungee.getLogger().log(Level.WARNING, "Since this version of the Badlion Mod API, the CPS API plugin is no longer necessary! Please delete it.");

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
		return this.apiBungee.getMessageSender();
	}
}