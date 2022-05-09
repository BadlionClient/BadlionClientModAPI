package net.badlion.bukkitapi;

import net.badlion.bukkitapi.command.SurvivalCommand;
import net.badlion.bukkitapi.gui.SurvivalGui;
import net.badlion.bukkitapi.listener.EnderchestListener;
import net.badlion.bukkitapi.survival.SurvivalManager;

import java.util.Objects;

public class BukkitBadlionPlugin extends AbstractBukkitBadlionPlugin {
	private SurvivalGui survivalGui;

	public BukkitBadlionPlugin() {
		this.setMessageSender(new BukkitPluginMessageSender(this));
		this.setSurvivalManager(new SurvivalManager(this));
	}

	@Override
	public void onEnable() {
		super.onEnable();

		this.survivalGui = new SurvivalGui(this);

		this.getServer().getPluginManager().registerEvents(new EnderchestListener(this), this);

		Objects.requireNonNull(this.getCommand("survivalapi")).setExecutor(new SurvivalCommand(this));
	}

	public SurvivalGui getSurvivalGui() {
		return this.survivalGui;
	}
}