package net.badlion.blcmodapibungee.listener;

import net.badlion.blcmodapibungee.BlcModApiBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class PlayerListener implements Listener {

	private BlcModApiBungee plugin;

	public PlayerListener(BlcModApiBungee plugin) {
		this.plugin = plugin;
	}


	@EventHandler
	public void onLogin(PostLoginEvent event) {
		// Send the disallowed mods to players when they login to the proxy. A notification will appear on the Badlion Client so they know the mod was disabled
		ProxiedPlayer player = event.getPlayer();
		if ( player.getPendingConnection().getVersion() <= ProtocolConstants.MINECRAFT_1_12_2 ) { // do not send to 1.13+ clients, this would break the connection
			player.unsafe().sendPacket( new PluginMessage( "BLC|M", BlcModApiBungee.GSON_NON_PRETTY.toJson( this.plugin.getConf().getModsDisallowed() ).getBytes(), false ) );
		}
	}
}
