package net.badlion.modapicommon.mods;

import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.Notification;

import java.util.UUID;

/**
 * Class with API Hooks for our notifications system, allowing for sending custom notifications to BLC users.
 */
public class Notifications {

	public static void sendNotification(UUID uuid, Notification notification) {
		if (uuid == null) {
			throw new IllegalStateException("uuid can't be null");
		}

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendData(uuid, ModType.NOTIFICATION.getType(), AbstractBadlionApi.GSON_NON_PRETTY.toJsonTree(notification));
	}
}
