package net.badlion.blcmodapibungee.listener;

import net.badlion.blcmodapibungee.BlcModApiBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class PlayerListener implements Listener {

	private BlcModApiBungee plugin;

	public PlayerListener(BlcModApiBungee plugin) {
		this.plugin = plugin;
	}


	@EventHandler
	public void onLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		player.unsafe().sendPacket(new PluginMessage("BLC|M", BlcModApiBungee.GSON_NON_PRETTY.toJson(this.plugin.getConf().getModsDisallowed()).getBytes(), false));
	}

}
