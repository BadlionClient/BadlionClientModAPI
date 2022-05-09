package net.badlion.bukkitapi.survival;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.badlion.bukkitapi.BukkitBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.mods.ModType;
import net.badlion.modapicommon.survival.SurvivalFeature;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

public class SurvivalManager extends AbstractBukkitSurvivalManager {
	private final BukkitBadlionPlugin apiBukkit;
	private final EnumSet<SurvivalFeature> enabledFeatures;
	private final SurvivalConfig config;
	private BukkitTask refreshTask;

	public SurvivalManager(BukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
		this.enabledFeatures = EnumSet.noneOf(SurvivalFeature.class);
		this.config = new SurvivalConfig();
		this.refreshTask = null;
	}

	@Override
	public void enableAllFeatures() {
		this.enabledFeatures.addAll(Arrays.asList(SurvivalFeature.values()));

		this.scheduleUpdate();
	}

	@Override
	public void disableAllFeatures() {
		this.enabledFeatures.clear();

		this.scheduleUpdate();
	}

	@Override
	public void enableFeature(SurvivalFeature feature) {
		this.enabledFeatures.add(feature);

		this.scheduleUpdate();
	}

	@Override
	public void disableFeature(SurvivalFeature feature) {
		this.enabledFeatures.remove(feature);

		this.scheduleUpdate();
	}

	@Override
	public Set<SurvivalFeature> getEnabledFeatures() {
		return Collections.unmodifiableSet(this.enabledFeatures);
	}

	@Override
	public void loadConfig() {
		File file = this.getConfigFile();

		if (!file.exists()) {
			this.apiBukkit.getLogger().info("No survival config found, creating an empty one");
			this.saveConfig();
			return;
		}

		SurvivalConfig survivalConfig;

		try (FileReader fileReader = new FileReader(file)) {
			survivalConfig = AbstractBadlionApi.GSON_NON_PRETTY.fromJson(fileReader, SurvivalConfig.class);

		} catch (Exception ex) {
			this.apiBukkit.getLogger().log(Level.SEVERE, "Failed to load survival config: " + ex.getMessage(), ex);
			return;
		}

		this.enabledFeatures.clear();

		for (String feature : survivalConfig.features) {
			try {
				this.enabledFeatures.add(SurvivalFeature.valueOf(feature));
			} catch (IllegalArgumentException ignored) {

			}
		}

		this.config.features = survivalConfig.features;
	}

	@Override
	public void sendData(Player player) {
		this.sendData(player, this.generateData());
	}

	private void sendData(Player player, JsonElement data) {
		this.apiBukkit.getMessageSender().sendModData(player.getUniqueId(), ModType.SURVIVAL, data);
	}

	private JsonElement generateData() {
		JsonObject jsonObject = AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(this.config).getAsJsonObject();

		jsonObject.add("type", new JsonPrimitive("config"));

		return jsonObject;
	}

	private void scheduleUpdate() {
		if (this.refreshTask != null) {
			return;
		}

		this.refreshTask = this.apiBukkit.getServer().getScheduler().runTaskLater(this.apiBukkit, () -> {
			this.refreshTask = null;

			this.config.features.clear();
			this.enabledFeatures.forEach(survivalFeature -> this.config.features.add(survivalFeature.name()));

			JsonElement data = this.generateData();
			this.apiBukkit.getServer().getOnlinePlayers().forEach(player -> this.sendData(player, data));

			this.saveConfig();

			this.apiBukkit.getSurvivalGui().refresh();
		}, 1L);
	}

	private File getConfigFile() {
		return new File(this.apiBukkit.getDataFolder(), "survival.json");
	}

	private void saveConfig() {
		File file = this.getConfigFile();

		File parent = file.getParentFile();

		if (!parent.exists() && !parent.mkdirs()) {
			this.apiBukkit.getLogger().warning("Failed to create survival config folder");
		}

		try (FileWriter fileWriter = new FileWriter(file)) {
			AbstractBadlionApi.GSON_PRETTY.toJson(this.config, fileWriter);

		} catch (Exception ex) {
			this.apiBukkit.getLogger().log(Level.SEVERE, "Failed to save survival config: " + ex.getMessage(), ex);
		}
	}
}
