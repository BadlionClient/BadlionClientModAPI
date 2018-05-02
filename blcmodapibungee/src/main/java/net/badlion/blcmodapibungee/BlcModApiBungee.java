package net.badlion.blcmodapibungee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.badlion.blcmodapibungee.listener.PlayerListener;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;

public class BlcModApiBungee extends Plugin {

	public static final Gson GSON_NON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
	public static final Gson GSON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create();

	private Conf conf;

	@Override
	public void onEnable() {
		if (!this.getDataFolder().exists()) {
			if (!this.getDataFolder().mkdir()) {
				this.getLogger().log(Level.SEVERE, "Failed to create plugin directory.");
			}
		}

		try {
			this.conf = loadConf(new File(this.getDataFolder(), "config.json"));

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

	}

	public Conf loadConf(File file) throws IOException {
		try (Reader reader = new BufferedReader(new FileReader(file))) {
			return BlcModApiBungee.GSON_NON_PRETTY.fromJson(reader, Conf.class);
		} catch (FileNotFoundException ex) {
			this.getLogger().log(Level.INFO,"No Config Found: Saving default...");
			Conf conf = new Conf();
			this.saveConf(conf, new File(this.getDataFolder(), "config.json"));
			return conf;
		}
	}

	private void saveConf(Conf conf, File file) {
		try (FileWriter writer = new FileWriter(file)) {
			BlcModApiBungee.GSON_PRETTY.toJson(conf, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Conf getConf() {
		return this.conf;
	}
}
