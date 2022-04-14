package net.badlion.bukkitapi.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.AbstractCosmeticManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticManager extends AbstractCosmeticManager implements Listener {
	private final Set<UUID> disabledNametags = Collections.newSetFromMap(new ConcurrentHashMap<>());

	@Override
	public void disableNametagCosmetics(UUID uuid) {
		if (!this.disabledNametags.add(uuid)) {
			return;
		}

		JsonObject data = this.getDisabledCosmeticsData(uuid, true, "nametag");

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendData("cosmetics", data);
	}

	@Override
	public void enableNametagCosmetics(UUID uuid) {
		if (!this.disabledNametags.remove(uuid)) {
			return;
		}

		JsonObject data = this.getDisabledCosmeticsData(uuid, false, "nametag");

		AbstractBadlionApi.getInstance().getPluginMessageSender().sendData("cosmetics", data);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.enableNametagCosmetics(event.getPlayer().getUniqueId());
	}

	public void onPlayerJoin(Player player) {
		if (!this.disabledNametags.isEmpty()) {
			for (UUID uuid : this.disabledNametags) {
				JsonObject data = this.getDisabledCosmeticsData(uuid, false, "nametag");

				AbstractBadlionApi.getInstance().getPluginMessageSender().sendData(player.getUniqueId(), "cosmetics", data);
			}
		}
	}

	private JsonObject getDisabledCosmeticsData(UUID uuid, boolean disabled, String... cosmeticTypes) {
		final JsonObject data = new JsonObject();
		final JsonArray array = new JsonArray();

		for (String cosmeticType : cosmeticTypes) {
			array.add(cosmeticType);
		}

		data.addProperty("type", "disable_cosmetics");
		data.add("cosmeticTypes", array);
		data.addProperty("disable", disabled);
		data.addProperty("uuid", uuid.toString());

		return data;
	}
}
