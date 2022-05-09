package net.badlion.bukkitapi.listener;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.badlion.bukkitapi.BukkitBadlionPlugin;
import net.badlion.modapicommon.mods.ModType;
import net.badlion.modapicommon.survival.SurvivalFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.logging.Level;

public class EnderchestListener implements Listener {
	private final BukkitBadlionPlugin apiBukkit;

	public EnderchestListener(BukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		this.sendEnderchest(event.getPlayer());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST && event.getWhoClicked() instanceof Player) {
			this.sendEnderchest((Player) event.getWhoClicked());
		}
	}

	public void sendEnderchest(Player player) {
		if (!this.apiBukkit.getBadlionApi().getSurvivalManager().getEnabledFeatures().contains(SurvivalFeature.SHOW_ENDERCHEST_INVENTORY)) {
			return;
		}

		this.apiBukkit.getServer().getScheduler().runTaskLater(this.apiBukkit, () -> {
			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				Inventory bukkitEnderChest = player.getEnderChest();
				Object enderchest = bukkitEnderChest.getClass().getDeclaredMethod("getInventory").invoke(bukkitEnderChest);
				Object listTag = enderchest.getClass().getDeclaredMethod("g").invoke(enderchest);

				Class<?> compoundTagClass = Class.forName("net.minecraft.nbt.NBTTagCompound");
				Object compoundTag = compoundTagClass.getConstructor().newInstance();

				compoundTagClass.getDeclaredMethod("a", String.class, Class.forName("net.minecraft.nbt.NBTBase")).invoke(compoundTag, "EnderChest", listTag);

				Class.forName("net.minecraft.nbt.NBTCompressedStreamTools").getDeclaredMethod("a", compoundTagClass, OutputStream.class).invoke(null, compoundTag, outputStream);

				JsonObject jsonObject = new JsonObject();

				jsonObject.add("type", new JsonPrimitive("enderchest"));
				jsonObject.add("value", new JsonPrimitive(Base64.getEncoder().encodeToString(outputStream.toByteArray())));

				this.apiBukkit.getBadlionApi().getPluginMessageSender().sendModData(player.getUniqueId(), ModType.SURVIVAL, jsonObject);
			} catch (Exception ex) {
				this.apiBukkit.getLogger().log(Level.SEVERE, "Failed to send enderchest to player " + player.getUniqueId() + ": " + ex.getMessage(), ex);
			}
		}, 1L);
	}
}
