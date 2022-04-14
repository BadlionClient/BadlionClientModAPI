package net.badlion.bukkitapi.timers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

public abstract class TimerApi {
	public static final String CHANNEL_NAME = "badlion:timers";
	public static TimerApi instance;

	/**
	 * Create a new timer and register it into the API.
	 * <p>
	 * A timer will automatically handle synchronizing with its receivers,
	 * and will repeat itself if it's mark as repeating. If not, it'll be
	 * automatically removed from the API.
	 *
	 * @param item      Item to show in the client
	 * @param repeating {@code true} if the timer is repeating, {@code false} otherwise
	 * @param time      Countdown time, in ticks (20 per seconds)
	 * @return The new timer instance
	 */
	public abstract Timer createTickTimer(ItemStack item, boolean repeating, long time);

	/**
	 * Create a new timer and register it into the API.
	 * <p>
	 * A timer will automatically handle synchronizing with its receivers,
	 * and will repeat itself if it's mark as repeating. If not, it'll be
	 * automatically removed from the API.
	 *
	 * @param name      Name to show in the client
	 * @param item      Item to show in the client
	 * @param repeating {@code true} if the timer is repeating, {@code false} otherwise
	 * @param time      Countdown time, in ticks (20 per seconds)
	 * @return The new timer instance
	 */
	public abstract Timer createTickTimer(String name, ItemStack item, boolean repeating, long time);

	/**
	 * Create a new timer and register it into the API.
	 * <p>
	 * A timer will automatically handle synchronizing with its receivers,
	 * and will repeat itself if it's mark as repeating. If not, it'll be
	 * automatically removed from the API.
	 *
	 * @param item      Item to show in the client
	 * @param repeating {@code true} if the timer is repeating, {@code false} otherwise
	 * @param time      Countdown time
	 * @param timeUnit  Countdown time unit
	 * @return The new timer instance
	 */
	public abstract Timer createTimeTimer(ItemStack item, boolean repeating, long time, TimeUnit timeUnit);

	/**
	 * Create a new timer and register it into the API.
	 * <p>
	 * A timer will automatically handle synchronizing with its receivers,
	 * and will repeat itself if it's mark as repeating. If not, it'll be
	 * automatically removed from the API.
	 *
	 * @param name      Name to show in the client
	 * @param item      Item to show in the client
	 * @param repeating {@code true} if the timer is repeating, {@code false} otherwise
	 * @param time      Countdown time
	 * @param timeUnit  Countdown time unit
	 * @return The new timer instance
	 */
	public abstract Timer createTimeTimer(String name, ItemStack item, boolean repeating, long time, TimeUnit timeUnit);

	/**
	 * Remove a timer from the API, disabling all API features about it.
	 *
	 * @param timer The timer instance to remove
	 */
	public abstract void removeTimer(Timer timer);

	/**
	 * Clear all timers for a player.
	 *
	 * @param player The player instance to remove
	 */
	public abstract void clearTimers(Player player);

	public static TimerApi getInstance() {
		return TimerApi.instance;
	}
}