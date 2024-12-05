package net.badlion.bukkitapi.survival;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.badlion.bukkitapi.AbstractBukkitBadlionPlugin;
import net.badlion.modapicommon.mods.ModType;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class BreedingTracker {

	private static final Object NO_VALUE = new Object();

	private final AbstractBukkitBadlionPlugin plugin;
	private final Cache<AgeCacheKey, Object> entityAgeSendCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

	public BreedingTracker(AbstractBukkitBadlionPlugin plugin) {
		this.plugin = plugin;
	}

	public void track(Player player, boolean checkCache, Entity... entities) {
		JsonArray array = new JsonArray();

		for (Entity entity : entities) {
			AgeCacheKey cacheKey = new AgeCacheKey(player.getUniqueId(), entity.getUniqueId());

			if (checkCache && this.entityAgeSendCache.getIfPresent(cacheKey) != null) {
				continue;
			}

			this.entityAgeSendCache.put(cacheKey, BreedingTracker.NO_VALUE);

			JsonObject entityObject = new JsonObject();
			entityObject.addProperty("id", entity.getUniqueId().toString());
			entityObject.addProperty("age", ((Ageable) entity).getAge());
			array.add(entityObject);
		}

		if (array.size() > 0) {
			JsonObject object = new JsonObject();
			object.addProperty("type", "breedingTracker");
			object.add("entities", array);
			this.plugin.getMessageSender().sendModData(player.getUniqueId(), ModType.SURVIVAL, object);
		}
	}

	public void trackAll(Player player) {
		Entity[] entities = player.getWorld().getEntities().stream().filter(e -> e instanceof Ageable && ((Ageable) e).getAge() != 0).toArray(Entity[]::new);
		this.track(player, false, entities);
	}

	private static class AgeCacheKey {
		private final UUID playerId;
		private final UUID entityId;

		public AgeCacheKey(UUID playerId, UUID entityId) {
			this.playerId = playerId;
			this.entityId = entityId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (o == null || this.getClass() != o.getClass()) {
				return false;
			}

			AgeCacheKey that = (AgeCacheKey) o;
			return Objects.equals(this.playerId, that.playerId) && Objects.equals(this.entityId, that.entityId);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.playerId, this.entityId);
		}
	}
}
