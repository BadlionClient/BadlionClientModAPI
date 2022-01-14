package net.badlion.velocityapi;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import net.badlion.modapicommon.AbstractPluginMessageSender;

import java.util.Optional;
import java.util.UUID;

public class VelocityPluginMessageSender extends AbstractPluginMessageSender {

	private final VelocityBadlionPlugin plugin;

	public VelocityPluginMessageSender(VelocityBadlionPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void sendPluginMessage(byte[] data) {
		for (Player player : this.plugin.getProxy().getAllPlayers()) {
			this.sendPluginMessagePacket(player, "badlion:modapi", data);
		}
	}

	@Override
	public void sendPluginMessage(UUID player, byte[] data) {
		Optional<Player> proxiedPlayer = this.plugin.getProxy().getPlayer(player);

		proxiedPlayer.ifPresent(value -> this.sendPluginMessagePacket(value, "badlion:modapi", data));
	}

	public void sendPluginMessagePacket(Player player, String channel, byte[] data) {
		player.sendPluginMessage(new LegacyChannelIdentifier(channel), data);
	}
}