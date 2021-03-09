package net.badlion.blcmodapivelocity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.badlion.blcmodapivelocity.listener.PlayerLoginListener;

import javax.inject.Inject;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Plugin(id = "blcmodapivelocity-3", name = "Badlion Client Velocity Api", version = "3.0-compatible", description = "Velocity Version of BLC's Bukkit & Bungee API", authors = {"Badlion", "AzraAnimating"})
public class BlcModApiVelocity {

	@Inject
	public BlcModApiVelocity(final ProxyServer server, final Logger logger) {
		this.server = server;
		this.logger = logger;
	}

	public static final Gson GSON_NON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().create();
	public static final Gson GSON_PRETTY = new GsonBuilder().enableComplexMapKeySerialization().disableHtmlEscaping().setPrettyPrinting().create();

	private final ProxyServer server;
	private final Logger logger;
	private File pluginFolder;

	private Conf conf;

	public void onEnable() {
		this.pluginFolder = new File("plugins/BadlionClientModAPI/");
		if (!pluginFolder.exists()) {
			if (!pluginFolder.mkdir()) {
				this.logger.log(Level.SEVERE, "Failed to create plugin directory.");
			}
		}

		try {
			this.conf = loadConf(new File(pluginFolder, "config.json"));

			// Only register the listener if the config loads successfully
			this.server.getEventManager().register(this, new PlayerLoginListener(this));

			this.logger.log(Level.INFO, "Successfully setup BadlionClientModAPI plugin.");
		} catch (IOException e) {
			this.logger.log(Level.SEVERE, "Error with config for BadlionClientModAPI plugin.");
			e.printStackTrace();
		}
	}

	@Subscribe
	public void onInitialize(ProxyInitializeEvent event) {
		this.onEnable();//Just kept all that from the original Bungee version, just tweaked a few things.
	}

	public Conf loadConf(File file) throws IOException {
		try (Reader reader = new BufferedReader(new FileReader(file))) {
			return BlcModApiVelocity.GSON_NON_PRETTY.fromJson(reader, Conf.class);
		} catch (FileNotFoundException ex) {
			this.logger.log(Level.INFO,"No Config Found: Saving default...");
			Conf conf = new Conf();
			this.saveConf(conf, new File(this.pluginFolder, "config.json"));
			return conf;
		}
	}

	private void saveConf(Conf conf, File file) {
		try (FileWriter writer = new FileWriter(file)) {
			BlcModApiVelocity.GSON_PRETTY.toJson(conf, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Conf getConf() {
		return this.conf;
	}
}
