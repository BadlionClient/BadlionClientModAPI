package net.badlion.blcmodapibukkit.timers;

import net.badlion.blcmodapibukkit.BlcModApiBukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimerApiImpl extends TimerApi {

	private final BlcModApiBukkit plugin;
	private final AtomicInteger idGenerator;
	private final Set<TimerImpl> allTimers;

	public TimerApiImpl(BlcModApiBukkit plugin) {
		TimerApi.instance = this;
		this.plugin = plugin;
		this.idGenerator = new AtomicInteger(1);
		this.allTimers = Collections.newSetFromMap(new ConcurrentHashMap<TimerImpl, Boolean>());
	}

	@Override
	public Timer createTickTimer(ItemStack item, boolean repeating, long time) {
		return this.createTickTimer(null, item, repeating, time);
	}

	@Override
	public Timer createTickTimer(String name, ItemStack item, boolean repeating, long time) {
		final TimerImpl timer = new TimerImpl(this.plugin, this.idGenerator.getAndIncrement(), name, item, repeating, time);
		this.allTimers.add(timer);

		return timer;
	}

	@Override
	public Timer createTimeTimer(ItemStack item, boolean repeating, long time, TimeUnit timeUnit) {
		return this.createTimeTimer(null, item, repeating, time, timeUnit);
	}

	@Override
	public Timer createTimeTimer(String name, ItemStack item, boolean repeating, long time, TimeUnit timeUnit) {
		final TimerImpl timer = new TimerImpl(this.plugin, this.idGenerator.getAndIncrement(), name, item, repeating, time, timeUnit);
		this.allTimers.add(timer);

		return timer;
	}

	@Override
	public void removeTimer(Timer timer) {
		// Failsafe
		if (timer instanceof TimerImpl) {
			TimerImpl timerImpl = (TimerImpl) timer;

			this.allTimers.remove(timerImpl);
		}

		timer.clearReceivers();
	}

	@Override
	public void clearTimers(Player player) {
		for (TimerImpl timer : this.allTimers) {
			timer.getReceivers().remove(player);
		}

		this.plugin.getMessageSender().sendPluginMessagePacket(player, TimerApi.CHANNEL_NAME, "REMOVE_ALL_TIMERS|{}".getBytes(StandardCharsets.UTF_8));
	}

	public void tickTimers() {
		for (TimerImpl timer : this.allTimers) {
			timer.tick();
		}
	}

	public void syncTimers() {
		for (TimerImpl timer : this.allTimers) {
			timer.syncTimer();
		}
	}
}
