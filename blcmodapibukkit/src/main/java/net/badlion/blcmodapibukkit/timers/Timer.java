package net.badlion.blcmodapibukkit.timers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface Timer {

	// Getters and setters

	/**
	 * Get the timer's id
	 *
	 * @return Id as unique long value
	 */
	long getId();

	/**
	 * Get the timer's name
	 *
	 * @return Name as string
	 */
	String getName();

	/**
	 * Set the timer's name
	 * Will be the text displayed in the client
	 *
	 * @param name Name as string
	 */
	void setName(String name);

	/**
	 * Get the item displayed in the client
	 *
	 * @return Item as a Bukkit ItemStack
	 */
	ItemStack getItem();

	/**
	 * Set the item displayed in the client
	 * Cannot be a null value
	 * <p>
	 * Note : Item metas are currently not implemented,
	 * but enchantments should work fine
	 *
	 * @param item Item as a Bukkit ItemStack
	 */
	void setItem(ItemStack item);

	/**
	 * Get if the timer is repeating or not
	 *
	 * @return Repeating value as a boolean
	 */
	boolean isRepeating();

	/**
	 * Set whether the timer should repeat when reaching 0 or not
	 *
	 * @param repeating {@code true} if repeating, {@code false} otherwise
	 */
	void setRepeating(boolean repeating);

	/**
	 * Get the timer countdown time
	 *
	 * @return Timer countdown time as a long number of ticks
	 */
	long getTime();

	/**
	 * Set the timer countdown time
	 * Note : This implies a call to {@link Timer#reset()}
	 *
	 * @param time Timer countdown time as a long number of ticks
	 */
	void setTime(long time);

	/**
	 * Get the timer countdown time
	 *
	 * @return Timer countdown time in milliseconds
	 */
	long getMillis();

	/**
	 * Set the timer countdown time
	 * Note : This implies a call to {@link Timer#reset()}
	 *
	 * @param time     Timer countdown time
	 * @param timeUnit Timer countdown time unit
	 */
	void setTime(long time, TimeUnit timeUnit);

	// Player functions

	/**
	 * Add a receiver to the timer
	 * Note : A disconnecting player will automatically
	 * be removed from the timer
	 *
	 * @param player Player instance to add
	 */
	void addReceiver(Player player);

	/**
	 * Manually remove a receiver form the timer
	 *
	 * @param player Player instance to remove
	 */
	void removeReceiver(Player player);

	/**
	 * Clear all players receiving this timer
	 */
	void clearReceivers();

	/**
	 * Get all the players that are receiving this timer
	 *
	 * @return Collection of receivers as a thread-safe collection
	 */
	Collection<Player> getReceivers();

	// Other functions

	/**
	 * Reset the current countdown to the {@link Timer#getTime()} value
	 */
	void reset();
}
