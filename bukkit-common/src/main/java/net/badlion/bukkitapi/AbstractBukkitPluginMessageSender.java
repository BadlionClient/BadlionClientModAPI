package net.badlion.bukkitapi;

import net.badlion.modapicommon.AbstractPluginMessageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AbstractBukkitPluginMessageSender extends AbstractPluginMessageSender {

	@Override
	public void sendPluginMessage(byte[] data) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.sendPluginMessagePacket(player, "badlion:modapi", data);
		}
	}

	@Override
	public void sendPluginMessage(UUID player, byte[] data) {
		final Player bukkitPlayer = Bukkit.getPlayer(player);

		if (bukkitPlayer != null) {
			this.sendPluginMessagePacket(bukkitPlayer, "badlion:modapi", data);
		}
	}

	public abstract void sendPluginMessagePacket(Player player, String channel, Object data);
}