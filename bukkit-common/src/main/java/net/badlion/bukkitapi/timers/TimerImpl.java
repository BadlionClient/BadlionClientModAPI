package net.badlion.bukkitapi.timers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.badlion.bukkitapi.AbstractBukkitBadlionPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimerImpl implements Timer {

	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(TimerImpl.class, new TimerSerializer())
		.create();

	private final AbstractBukkitBadlionPlugin plugin;

	private final long id;
	// Optimization
	private final RemoveReceiverRequest removeReceiverRequest;
	private String name;
	private ItemStack item;
	private boolean repeating;
	private long time;
	private long millis;
	private final AtomicBoolean updated;
	private final Set<Player> receivers;
	private long currentTime;
	private long lastTick;

	public TimerImpl(AbstractBukkitBadlionPlugin plugin, long id, String name, ItemStack item, boolean repeating, long time) {
		this.plugin = plugin;
		this.id = id;
		this.name = name;
		this.item = item;
		this.repeating = repeating;
		this.time = time;
		this.millis = -1;
		this.currentTime = time;
		this.lastTick = System.currentTimeMillis();

		this.updated = new AtomicBoolean(false);
		this.receivers = Collections.newSetFromMap(new ConcurrentHashMap<>());

		this.removeReceiverRequest = new RemoveReceiverRequest();
		this.removeReceiverRequest.id = id;
	}

	public TimerImpl(AbstractBukkitBadlionPlugin plugin, int id, String name, ItemStack item, boolean repeating, long time, TimeUnit timeUnit) {
		this.plugin = plugin;
		this.id = id;
		this.name = name;
		this.item = item;
		this.repeating = repeating;
		this.time = -1;
		this.millis = timeUnit.toMillis(time);
		this.currentTime = this.millis;
		this.lastTick = System.currentTimeMillis();

		this.updated = new AtomicBoolean(false);
		this.receivers = Collections.newSetFromMap(new ConcurrentHashMap<>());

		this.removeReceiverRequest = new RemoveReceiverRequest();
		this.removeReceiverRequest.id = id;
	}

	@Override
	public long getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		String old = this.name;

		this.name = name;

		if (!old.equals(name)) {
			this.updated.set(true);
		}
	}

	@Override
	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public void setItem(ItemStack item) {
		ItemStack old = this.item;

		this.item = item;

		if (old != item) {
			this.updated.set(true);
		}
	}

	@Override
	public boolean isRepeating() {
		return this.repeating;
	}

	@Override
	public void setRepeating(boolean repeating) {
		boolean old = this.repeating;

		this.repeating = repeating;

		if (old != repeating) {
			this.updated.set(true);
		}
	}

	@Override
	public long getTime() {
		return this.time;
	}

	@Override
	public void setTime(long time) {
		this.time = time;
		this.millis = -1L;
		this.updated.set(true);
		this.reset();
	}

	@Override
	public long getMillis() {
		return this.millis;
	}

	@Override
	public void setTime(long time, TimeUnit timeUnit) {
		this.time = -1L;
		this.millis = timeUnit.toMillis(time);
		this.updated.set(true);
		this.reset();
	}

	@Override
	public void addReceiver(Player player) {
		if (this.receivers.add(player)) {
			this.send(player, "ADD_TIMER", this);
		}
	}

	@Override
	public void removeReceiver(Player player) {
		if (this.receivers.remove(player)) {
			this.send(player, "REMOVE_TIMER", this.removeReceiverRequest);
		}
	}

	@Override
	public void clearReceivers() {
		this.send(this.receivers, "REMOVE_TIMER", this.removeReceiverRequest);

		this.receivers.clear();
	}

	@Override
	public Collection<Player> getReceivers() {
		return this.receivers;
	}

	@Override
	public void reset() {
		this.currentTime = this.time != -1L ? this.time : this.millis;

		this.syncTimer();
	}

	public void tick() {
		long currentMillis = System.currentTimeMillis();

		if (this.time != -1L) {
			if (--this.currentTime <= 0) {
				if (!this.repeating) {
					this.plugin.getTimerApi().removeTimer(this);
					return;
				} else {
					this.currentTime = this.time;
				}
			}
		} else {
			long diff = currentMillis - this.lastTick;

			if ((this.currentTime -= diff) <= 0) {
				if (!this.repeating) {
					this.plugin.getTimerApi().removeTimer(this);
					return;
				} else {
					this.currentTime += this.millis;
				}
			}
		}

		this.lastTick = currentMillis;

		if (this.updated.compareAndSet(true, false)) {
			this.send(this.receivers, "UPDATE_TIMER", this);
		}
	}

	public void syncTimer() {
		SyncTimerRequest syncTimerRequest = new SyncTimerRequest();
		syncTimerRequest.id = this.id;
		syncTimerRequest.time = this.currentTime;

		this.send(this.receivers, "SYNC_TIMERS", syncTimerRequest);
	}

	private <T> void send(Player player, String requestName, T request) {
		this.plugin.getMessageSender().sendPluginMessagePacket(player, TimerApi.CHANNEL_NAME, (requestName + "|" + TimerImpl.GSON.toJson(request)).getBytes(StandardCharsets.UTF_8));
	}

	private <T> void send(Collection<Player> players, String requestName, T request) {
		byte[] data = (requestName + "|" + TimerImpl.GSON.toJson(request)).getBytes(StandardCharsets.UTF_8);

		for (Player player : players) {
			this.plugin.getMessageSender().sendPluginMessagePacket(player, TimerApi.CHANNEL_NAME, data);
		}
	}

	private static class TimerSerializer implements JsonSerializer<TimerImpl> {
		@Override
		public JsonElement serialize(TimerImpl timer, Type type, JsonSerializationContext jsonSerializationContext) {
			final JsonObject jsonObject = new JsonObject();

			jsonObject.add("id", new JsonPrimitive(timer.id));
			jsonObject.add("name", timer.name == null ? JsonNull.INSTANCE : new JsonPrimitive(timer.name));
			jsonObject.add("item", TimerImpl.GSON.toJsonTree(timer.item.serialize()));
			jsonObject.add("repeating", new JsonPrimitive(timer.repeating));
			jsonObject.add("time", new JsonPrimitive(timer.time));
			jsonObject.add("millis", new JsonPrimitive(timer.millis));
			jsonObject.add("currentTime", new JsonPrimitive(timer.currentTime));

			return jsonObject;
		}
	}

	private static class RemoveReceiverRequest {
		private long id;
	}

	private static class SyncTimerRequest {
		private long id;
		private long time;
	}
}