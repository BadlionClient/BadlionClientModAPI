package net.badlion.modapicommon.survival;

import java.util.Arrays;
import java.util.List;

public enum SurvivalFeature {
	LOCK_ITEMS_IN_INVENTORY(
		"CHEST",
		"Lock items in inventory",
		"..."
	),
	ANTI_BREAKING_TOOL(
		"STONE_PICKAXE",
		"Anti breaking tools",
		"..."
	),
	HIGHLIGHT_SHULKER_PROJECTILES(
		"SHULKER_SHELL",
		"Highlight shulker projectiles",
		"..."
	),
	ANTI_AMETHYST_MINING(
		"BUDDING_AMETHYST",
		"Anti amethyst mining",
		"..."
	),
	SHOW_SHULKER_INVENTORY(
		"WHITE_SHULKER_BOX",
		"Show shulker inventory",
		"..."
	),
	SHOW_ENDERCHEST_INVENTORY(
		"ENDER_CHEST",
		"Show enderchest inventory",
		"..."
	),
	SEARCH_BAR(
		"SPYGLASS",
		"Inventory search bar",
		"..."
	),
	REMOVE_ELDER_GUARDIAN_EFFECT(
		"ELDER_GUARDIAN_SPAWN_EGG",
		"Remove elder guardian effect",
		"..."
	),
	ANTI_STRIP_LOG(
		"OAK_LOG",
		"Anti strip log",
		"..."
	),
	DEATH_COUNTER(
		"REDSTONE",
		"Death counter",
		"..."
	),
	DEATH_FINDER(
		"COMPASS",
		"Death finder",
		"..."
	),
	TOOL_INFO(
		"IRON_PICKAXE",
		"Tool stats",
		"..."
	),
	INVENTORY_MOVE(
		"CRAFTING_TABLE",
		"Inventory move",
		"..."
	);

	private final String material;
	private final String name;
	private final List<String> lore;

	SurvivalFeature(String material, String name, String... lore) {
		this.material = material;
		this.name = name;
		this.lore = Arrays.asList(lore);
	}

	public String getMaterial() {
		return this.material;
	}

	public String getName() {
		return this.name;
	}

	public List<String> getLore() {
		return this.lore;
	}
}
