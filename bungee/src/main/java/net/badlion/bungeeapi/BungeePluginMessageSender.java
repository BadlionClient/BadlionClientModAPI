package net.badlion.bungeeapi;

import net.badlion.modapicommon.AbstractPluginMessageSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.util.UUID;

public class BungeePluginMessageSender extends AbstractPluginMessageSender {

	private final BungeeBadlionPlugin apiBungee;

	public BungeePluginMessageSender(BungeeBadlionPlugin apiBungee) {
		this.apiBungee = apiBungee;
	}

	@Override
	public void sendPluginMessage(byte[] data) {
		for (ProxiedPlayer player : this.apiBungee.getProxy().getPlayers()) {
			this.sendPluginMessagePacket(player, "badlion:modapi", data);
		}
	}

	@Override
	public void sendPluginMessage(UUID player, byte[] data) {
		ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);

		if (proxiedPlayer != null) {
			this.sendPluginMessagePacket(proxiedPlayer, "badlion:modapi", data);
		}
	}

	public void sendPluginMessagePacket(ProxiedPlayer player, String channel, byte[] data) {
		player.unsafe().sendPacket(new PluginMessage(channel, data, false));
	}
}