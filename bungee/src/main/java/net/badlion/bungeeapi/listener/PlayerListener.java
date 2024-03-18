package net.badlion.bungeeapi.listener;

import net.badlion.bungeeapi.BungeeBadlionPlugin;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class PlayerListener implements Listener {

	private final Method serverSwitchEventGetFromMethod;
	private final BungeeBadlionPlugin plugin;

	public PlayerListener(BungeeBadlionPlugin plugin) {
		this.plugin = plugin;

		Method method;

		try {
			//noinspection JavaReflectionMemberAccess
			method = ServerSwitchEvent.class.getDeclaredMethod("getFrom");
		} catch (NoSuchMethodException ignored) {
			method = null;

			this.plugin.getLogger().warning("ServerSwitchEvent.getFrom() was not found, this BungeeCord version is maybe outdated and might not support 1.20.3+ properly");
		}

		this.serverSwitchEventGetFromMethod = method;
	}

	@EventHandler
	public void onLogin(PostLoginEvent event) {
		// On 1.20.3+, the player is still in LOGIN protocol at this point, we can't send that packet yet
		if (event.getPlayer().getPendingConnection().getVersion() < 764) {
			this.sendModPacket(event.getPlayer());
		}
	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
		// Send it on the initial server switch instead
		if (event.getPlayer().getPendingConnection().getVersion() >= 764 && this.serverSwitchEventGetFromMethod != null) {
			boolean initialLogin;

			try {
				initialLogin = this.serverSwitchEventGetFromMethod.invoke(event) == null;
			} catch (IllegalAccessException | InvocationTargetException ignored) {
				initialLogin = false;
			}

			if (initialLogin) {
				this.sendModPacket(event.getPlayer());
			}
		}
	}

	private void sendModPacket(ProxiedPlayer player) {
		this.plugin.getLogger().severe(AbstractBadlionApi.GSON_NON_PRETTY.toJson(this.plugin.getBadlionApi().getBadlionConfig()));
		// Send the disallowed mods to players when they log in to the proxy. A notification will appear on the Badlion Client, so they know the mod was disabled
		player.unsafe().sendPacket(new PluginMessage("badlion:modapi", AbstractBadlionApi.GSON_NON_PRETTY.toJson(this.plugin.getBadlionApi().getBadlionConfig()).getBytes(), false));
	}
}
