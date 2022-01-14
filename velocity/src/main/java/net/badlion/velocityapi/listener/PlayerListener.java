package net.badlion.velocityapi.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.badlion.velocityapi.VelocityBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;

public class PlayerListener {

	private final VelocityBadlionPlugin plugin;

	public PlayerListener(VelocityBadlionPlugin plugin) {
		this.plugin = plugin;
	}

	@Subscribe
	public void onLogin(PostLoginEvent event) {
		// Send the disallowed mods to players when they login to the proxy. A notification will appear on the Badlion Client so they know the mod was disabled
		Player player = event.getPlayer();
		player.sendPluginMessage(MinecraftChannelIdentifier.from("badlion:modapi"), AbstractBadlionApi.GSON_NON_PRETTY.toJson(this.plugin.getBadlionApi().getBadlionConfig()).getBytes());
	}
}
