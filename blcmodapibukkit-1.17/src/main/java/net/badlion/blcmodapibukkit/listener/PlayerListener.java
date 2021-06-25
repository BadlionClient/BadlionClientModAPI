package net.badlion.blcmodapibukkit.listener;

import net.badlion.blcmodapibukkit.BlcModApiBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class PlayerListener implements Listener {
	private final BlcModApiBukkit plugin;
	private String versionSuffix;

	private final Method getHandleMethod;

	private final Field playerConnectionField;

	private final Method sendPacketMethod;

	private Constructor<?> packetPlayOutCustomPayloadConstructor;
	private Constructor<?> packetPlayOutMinecraftKeyConstructor;
	private boolean useMinecraftKey;

	// Bukkit 1.8+ support
	private Class<?> packetDataSerializerClass;
	private Constructor<?> packetDataSerializerConstructor;

	private Method wrappedBufferMethod;

	public PlayerListener(BlcModApiBukkit plugin) {
		this.plugin = plugin;

		// Get the v1_X_Y from the end of the package name, e.g. v_1_7_R4 or v_1_12_R1
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		String[] parts = packageName.split("\\.");

		if (parts.length > 0) {
			String suffix = parts[parts.length - 1];
			if (!suffix.startsWith("v")) {
				throw new RuntimeException("Failed to find version for running Minecraft server, got suffix " + suffix);
			}

			this.versionSuffix = suffix;

			this.plugin.getLogger().info("Found version " + this.versionSuffix);
		}

		// We need to use reflection because Bukkit by default handles plugin messages in a really silly way
		// Reflection stuff
		Class<?> craftPlayerClass = this.getClass("org.bukkit.craftbukkit." + this.versionSuffix + ".entity.CraftPlayer");
		if (craftPlayerClass == null) {
			throw new RuntimeException("Failed to find CraftPlayer class");
		}

		Class<?> nmsPlayerClass = this.getClass("net.minecraft.server.level.EntityPlayer");
		if (nmsPlayerClass == null) {
			throw new RuntimeException("Failed to find EntityPlayer class");
		}

		Class<?> playerConnectionClass = this.getClass("net.minecraft.server.network.PlayerConnection");
		if (playerConnectionClass == null) {
			throw new RuntimeException("Failed to find PlayerConnection class");
		}

		Class<?> packetPlayOutCustomPayloadClass = this.getClass("net.minecraft.network.protocol.game.PacketPlayOutCustomPayload");
		if (packetPlayOutCustomPayloadClass == null) {
			throw new RuntimeException("Failed to find PacketPlayOutCustomPayload class");
		}

		this.packetPlayOutCustomPayloadConstructor = this.getConstructor(packetPlayOutCustomPayloadClass, String.class, byte[].class);
		if (this.packetPlayOutCustomPayloadConstructor == null) {
			// Newer versions of Minecraft use a different custom packet system
			this.packetDataSerializerClass = this.getClass("net.minecraft.network.PacketDataSerializer");
			if (this.packetDataSerializerClass == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or PacketDataSerializer class");
			}

			// Netty classes used by newer 1.8 and newer
			Class<?> byteBufClass = this.getClass("io.netty.buffer.ByteBuf");
			if (byteBufClass == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or ByteBuf class");
			}

			this.packetDataSerializerConstructor = this.getConstructor(this.packetDataSerializerClass, byteBufClass);
			if (this.packetDataSerializerConstructor == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or PacketDataSerializer constructor");
			}

			Class<?> unpooledClass = this.getClass("io.netty.buffer.Unpooled");
			if (unpooledClass == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or Unpooled class");
			}

			this.wrappedBufferMethod = this.getMethod(unpooledClass, "wrappedBuffer", byte[].class);
			if (this.wrappedBufferMethod == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor or wrappedBuffer()");
			}

			// If we made it this far in theory we are on at least 1.8
			this.packetPlayOutCustomPayloadConstructor = this.getConstructor(packetPlayOutCustomPayloadClass, String.class, this.packetDataSerializerClass);
			if (this.packetPlayOutCustomPayloadConstructor == null) {
				Class<?> minecraftKeyClass = this.getClass("net.minecraft.resources.MinecraftKey");

				// Fix for Paper in newer versions
				this.packetPlayOutCustomPayloadConstructor = this.getConstructor(packetPlayOutCustomPayloadClass, minecraftKeyClass, this.packetDataSerializerClass);

				if (this.packetPlayOutCustomPayloadConstructor == null) {
					throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor 2x");
				} else {
					this.useMinecraftKey = true;
					this.packetPlayOutMinecraftKeyConstructor = this.getConstructor(minecraftKeyClass, String.class);
				}
			}
		}

		this.getHandleMethod = this.getMethod(craftPlayerClass, "getHandle");
		if (this.getHandleMethod == null) {
			throw new RuntimeException("Failed to find CraftPlayer.getHandle()");
		}

		this.playerConnectionField = this.getField(nmsPlayerClass, "b");
		if (this.playerConnectionField == null) {
			throw new RuntimeException("Failed to find EntityPlayer.playerConnection");
		}

		this.sendPacketMethod = this.getMethod(playerConnectionClass, "sendPacket");
		if (this.sendPacketMethod == null) {
			throw new RuntimeException("Failed to find PlayerConnection.sendPacket()");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		// Create data we need for packet;
		String channel = "badlion:mods";
		Object message = BlcModApiBukkit.GSON_NON_PRETTY.toJson(this.plugin.getConf().getModsDisallowed()).getBytes();

		try {
			Object packet;
			// Newer MC version, setup ByteBuf object
			if (this.packetDataSerializerClass != null) {
				Object byteBuf = this.wrappedBufferMethod.invoke(null, message);
				Object packetDataSerializer = this.packetDataSerializerConstructor.newInstance(byteBuf);

				if (this.useMinecraftKey) {
					Object key = this.packetPlayOutMinecraftKeyConstructor.newInstance(channel);
					packet = this.packetPlayOutCustomPayloadConstructor.newInstance(key, packetDataSerializer);
				} else {
					packet = this.packetPlayOutCustomPayloadConstructor.newInstance(channel, packetDataSerializer);
				}
			} else {
				// Work our magic to make the packet
				packet = this.packetPlayOutCustomPayloadConstructor.newInstance(channel, message);
			}

			// Work our magic to send the packet
			Object nmsPlayer = this.getHandleMethod.invoke(player);
			Object playerConnection = this.playerConnectionField.get(nmsPlayer);
			this.sendPacketMethod.invoke(playerConnection, packet);
		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
			this.plugin.getLogger().severe("Failed to send BLC mod packet");
			e.printStackTrace();
		}
	}

	public Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
		for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (Arrays.equals(constructor.getParameterTypes(), params)) {
				constructor.setAccessible(true);
				return constructor;
			}
		}

		return null;
	}

	public Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
		for (final Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName)) {
				if (params.length > 0) {
					if (Arrays.equals(method.getParameterTypes(), params)) {
						method.setAccessible(true);
						return method;
					}
				} else {
					method.setAccessible(true);
					return method;
				}
			}
		}

		return null;
	}

	public Field getField(Class<?> clazz, String fieldName) {
		for (final Field field : clazz.getDeclaredFields()) {
			if (field.getName().equals(fieldName)) {
				field.setAccessible(true);
				return field;
			}
		}

		return null;
	}

}
