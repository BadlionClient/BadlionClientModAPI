package net.badlion.bukkitapi.command;

import net.badlion.bukkitapi.BukkitBadlionPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SurvivalCommand implements CommandExecutor {
	private final BukkitBadlionPlugin apiBukkit;

	public SurvivalCommand(BukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage("This command can only be used by a player.");
			return true;
		}

		this.apiBukkit.getSurvivalGui().open((Player) commandSender);

		return true;
	}
}
