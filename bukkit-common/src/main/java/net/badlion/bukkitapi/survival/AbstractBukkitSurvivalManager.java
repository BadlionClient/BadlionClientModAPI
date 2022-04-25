package net.badlion.bukkitapi.survival;

import net.badlion.modapicommon.survival.AbstractSurvivalManager;
import org.bukkit.entity.Player;

public abstract class AbstractBukkitSurvivalManager extends AbstractSurvivalManager {
	public abstract void loadConfig();

	public abstract void sendData(Player player);
}
