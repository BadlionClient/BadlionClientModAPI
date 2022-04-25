package net.badlion.bukkitapi;

import net.badlion.bukkitapi.survival.UnsupportedSurvivalManager;

public class BukkitBadlionPlugin extends AbstractBukkitBadlionPlugin {
	public BukkitBadlionPlugin() {
		this.setMessageSender(new BukkitPluginMessageSender(this));
		this.setSurvivalManager(new UnsupportedSurvivalManager());
	}
}