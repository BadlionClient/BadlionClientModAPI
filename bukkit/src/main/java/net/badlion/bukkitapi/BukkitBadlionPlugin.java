package net.badlion.bukkitapi;

public class BukkitBadlionPlugin extends AbstractBukkitBadlionPlugin {
	public BukkitBadlionPlugin() {
		this.setMessageSender(new BukkitPluginMessageSender(this));
	}
}