package net.badlion.bukkitapi.gui;

import net.badlion.bukkitapi.BukkitBadlionPlugin;
import net.badlion.modapicommon.survival.SurvivalApi;
import net.badlion.modapicommon.survival.SurvivalFeature;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SurvivalGui implements Listener {
	private static final String INVENTORY_NAME = "Badlion - Survival Mod Features";

	private final Inventory inventory;
	private final Map<Integer, String> actions;

	public SurvivalGui(BukkitBadlionPlugin apiBukkit) {
		apiBukkit.getServer().getPluginManager().registerEvents(this, apiBukkit);

		int size = 27;
		this.inventory = apiBukkit.getServer().createInventory(null, size, SurvivalGui.INVENTORY_NAME);
		this.actions = new HashMap<>();

		this.refresh();

		this.setCloseItem(size - 1);
	}

	public void refresh() {
		Set<SurvivalFeature> enabledFeatures = SurvivalApi.getEnabledFeatures();
		int index = 0;

		for (SurvivalFeature feature : SurvivalFeature.values()) {
			this.setItem(enabledFeatures, index++, feature);
		}
	}

	public void open(Player player) {
		player.openInventory(this.inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTopInventory() == this.inventory) {
			event.setCancelled(true);

			String action = this.actions.get(event.getRawSlot());

			if (action != null) {
				if ("CLOSE".equals(action)) {
					event.getWhoClicked().closeInventory();

				} else {
					SurvivalFeature survivalFeature = null;

					try {
						survivalFeature = SurvivalFeature.valueOf(action);
					} catch (IllegalArgumentException ignored) {

					}

					if (survivalFeature != null) {
						boolean enabled = SurvivalApi.getEnabledFeatures().contains(survivalFeature);

						if (enabled) {
							SurvivalApi.disableFeature(survivalFeature);
						} else {
							SurvivalApi.enableFeature(survivalFeature);
						}

						event.getWhoClicked().sendMessage(ChatColor.GREEN + "You " + (enabled ? "disabled " : "enabled ") + survivalFeature.getName());

						if (event.getWhoClicked() instanceof Player) { // Should always be true
							((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, enabled ? 1 : 2);
						}
					}
				}
			}
		}
	}

	private void setCloseItem(int index) {
		ItemStack closeItem = new ItemStack(Material.BARRIER);
		ItemMeta itemMeta = closeItem.getItemMeta();

		if (itemMeta != null) {
			itemMeta.setDisplayName(ChatColor.RED + "Close");
			closeItem.setItemMeta(itemMeta);
		}

		this.setItem(index, closeItem, "CLOSE");
	}

	private void setItem(Set<SurvivalFeature> enabledFeatures, int index, SurvivalFeature feature) {
		Material material = Material.getMaterial(feature.getMaterial());

		ItemStack itemStack = new ItemStack(material == null ? Material.STONE : material);
		ItemMeta itemMeta = itemStack.getItemMeta();

		if (itemMeta != null) {
			boolean enabled = enabledFeatures.contains(feature);

			List<String> lore = new ArrayList<>();
			lore.add("");
			feature.getLore().forEach(line -> lore.add(ChatColor.GRAY + line));
			lore.add("");
			lore.add(ChatColor.YELLOW + "Click to " + (enabled ? "disable" : "enable"));

			itemMeta.setDisplayName((enabled ? ChatColor.GREEN : ChatColor.RED) + feature.getName());
			itemMeta.setLore(lore);
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

			if (enabled) {
				itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
				itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}

			itemStack.setItemMeta(itemMeta);
		}

		this.setItem(index, itemStack, feature.name());
	}

	private void setItem(int index, ItemStack itemStack, String action) {
		this.inventory.setItem(index, itemStack);
		this.actions.put(index, action);
	}
}
