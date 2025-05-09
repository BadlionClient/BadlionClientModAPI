package net.badlion.bukkitapi;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.logging.Level;

public class BukkitPluginMessageSender extends AbstractBukkitPluginMessageSender {

	private final AbstractBukkitBadlionPlugin apiBukkit;

	private String versionSuffix;

	private Class<?> packetDataSerializerClass;
	private Class<?> minecraftKeyClass;
	private Class<?> customPacketPayloadClass;

	private Constructor<?> packetPlayOutCustomPayloadConstructor;
	private Constructor<?> packetDataSerializerConstructor;
	private Constructor<?> packetPlayOutMinecraftKeyConstructor;
	private Constructor<?> discardedPayloadConstructor;

	private Method sendCustomPayloadMethod;
	private Method wrappedBufferMethod;
	private Method packetDataSerializerWriteBytesMethod;
	private Method resourceLocationParseMethod;
	private Method getHandleMethod;
	private Method sendPacketMethod;

	private Field playerConnectionField;

	private boolean useMinecraftKey;
	private boolean usePacketPayload;
	private boolean useDiscardedPayload;
	private boolean useDiscardedPayloadByteArray;

	public BukkitPluginMessageSender(AbstractBukkitBadlionPlugin apiBukkit) {
		this.apiBukkit = apiBukkit;

		// Get the v1_X_Y from the end of the package name, e.g. v_1_7_R4 or v_1_12_R1
		String packageName = this.apiBukkit.getServer().getClass().getPackage().getName();
		String[] parts = packageName.split("\\.");

		if (parts.length > 0) {
			String suffix = parts[parts.length - 1];
			if (!suffix.startsWith("v")) {
				// 1.20.5+ support
				if ("craftbukkit".equals(suffix)) {
					suffix = "";
				} else {
					throw new RuntimeException("Failed to find version for running Minecraft server, got suffix " + suffix);
				}
			}

			this.versionSuffix = suffix;

			String versionName = this.apiBukkit.getServer().getVersion();

			this.apiBukkit.getLogger().info("Found version " + this.versionSuffix + " (" + versionName + ")");
		}

		// We need to use reflection because Bukkit by default handles plugin messages in a really silly way
		// Reflection stuff
		Class<?> craftPlayerClass = this.getClass(this.versionSuffix == null || this.versionSuffix.isEmpty() ? "org.bukkit.craftbukkit.entity.CraftPlayer" : "org.bukkit.craftbukkit." + this.versionSuffix + ".entity.CraftPlayer");
		if (craftPlayerClass == null) {
			throw new RuntimeException("Failed to find CraftPlayer class");
		}

		// Paper method added in 1.20.2+, hopefully should never change
		this.minecraftKeyClass = this.getClass("net.minecraft.resources.MinecraftKey");
		if (this.minecraftKeyClass != null) {
			this.sendCustomPayloadMethod = this.getMethod(craftPlayerClass, "sendCustomPayload", this.minecraftKeyClass, byte[].class);

			if (this.sendCustomPayloadMethod != null) {
				this.packetPlayOutMinecraftKeyConstructor = this.getConstructor(this.minecraftKeyClass, String.class);

				if (this.packetPlayOutMinecraftKeyConstructor == null) {
					this.resourceLocationParseMethod = this.getMethod(this.minecraftKeyClass, "parse", String.class);

					if (this.resourceLocationParseMethod == null) {
						this.resourceLocationParseMethod = this.getMethod(this.minecraftKeyClass, "a", String.class);
					}
				}

				return;
			}
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
			packetPlayOutCustomPayloadClass = this.getClass("net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket");

			if (packetPlayOutCustomPayloadClass == null) {
				throw new RuntimeException("Failed to find PacketPlayOutCustomPayload class");
			}
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
				// Fix for Paper in newer versions
				this.packetPlayOutCustomPayloadConstructor = this.getConstructor(packetPlayOutCustomPayloadClass, this.minecraftKeyClass, this.packetDataSerializerClass);

				if (this.packetPlayOutCustomPayloadConstructor == null) {
					this.customPacketPayloadClass = this.getClass("net.minecraft.network.protocol.common.custom.CustomPacketPayload");

					if (this.customPacketPayloadClass != null) {
						this.packetPlayOutCustomPayloadConstructor = this.getConstructor(packetPlayOutCustomPayloadClass, this.customPacketPayloadClass);
						this.packetDataSerializerWriteBytesMethod = this.getMethod(this.packetDataSerializerClass, "c", byte[].class);
						this.packetPlayOutMinecraftKeyConstructor = this.getConstructor(this.minecraftKeyClass, String.class);
						this.usePacketPayload = true;

						Class<?> discardedPayloadClass = this.getClass("net.minecraft.network.protocol.common.custom.DiscardedPayload");

						if (discardedPayloadClass != null) {
							this.discardedPayloadConstructor = this.getConstructor(discardedPayloadClass, this.minecraftKeyClass, byteBufClass);

							if (this.discardedPayloadConstructor != null) {
								this.useDiscardedPayload = true;
							} else {
								this.discardedPayloadConstructor = this.getConstructor(discardedPayloadClass, this.minecraftKeyClass, byte[].class);

								if (this.discardedPayloadConstructor != null) {
									this.useDiscardedPayloadByteArray = true;
								}
							}
						}

						// 1.21+
						if (this.packetPlayOutMinecraftKeyConstructor == null) {
							this.resourceLocationParseMethod = this.getMethod(this.minecraftKeyClass, "parse", String.class);
						}
					}

					if (this.packetPlayOutCustomPayloadConstructor == null) {
						throw new RuntimeException("Failed to find PacketPlayOutCustomPayload constructor 2x");
					}
				} else {
					this.useMinecraftKey = true;
					this.packetPlayOutMinecraftKeyConstructor = this.getConstructor(this.minecraftKeyClass, String.class);
				}
			}
		}

		this.getHandleMethod = this.getMethod(craftPlayerClass, "getHandle");
		if (this.getHandleMethod == null) {
			throw new RuntimeException("Failed to find CraftPlayer.getHandle()");
		}

		Field playerConnectionField;

		if (this.versionSuffix.contains("v1_17") || this.versionSuffix.contains("v1_18") || this.versionSuffix.contains("v1_19")) {
			playerConnectionField = this.getField(nmsPlayerClass, "b");
		} else {
			playerConnectionField = this.getField(nmsPlayerClass, "c");
		}

		if (playerConnectionField == null) {
			playerConnectionField = this.getField(nmsPlayerClass, "connection");
		}

		if (playerConnectionField != null) {
			this.playerConnectionField = playerConnectionField;
		} else {
			throw new RuntimeException("Failed to find EntityPlayer.playerConnection");
		}

		if (!this.versionSuffix.contains("v1_17")) {
			final Class<?> packet1_18Class = this.getClass("net.minecraft.network.protocol.Packet");
			Method sendPacketMethod;

			if (this.usePacketPayload) {
				sendPacketMethod = this.getMethod(playerConnectionClass.getSuperclass(), "b", packet1_18Class);
			} else {
				sendPacketMethod = this.getMethod(playerConnectionClass, "a", packet1_18Class);
			}

			if (sendPacketMethod == null) {
				sendPacketMethod = this.getMethod(playerConnectionClass.getSuperclass(), "send", packet1_18Class);
			}

			if (sendPacketMethod != null) {
				this.sendPacketMethod = sendPacketMethod;
			} else {
				throw new RuntimeException("Failed to find PlayerConnection.send(Packet)");
			}

		} else {
			this.sendPacketMethod = this.getMethod(playerConnectionClass, "sendPacket");

			if (this.sendPacketMethod == null) {
				throw new RuntimeException("Failed to find PlayerConnection.sendPacket()");
			}
		}
	}

	@Override
	public void sendPluginMessagePacket(Player player, String channel, Object data) {
		try {
			Object packet;

			if (this.sendCustomPayloadMethod != null) {
				Object key;

				if (this.packetPlayOutMinecraftKeyConstructor == null) {
					key = this.resourceLocationParseMethod.invoke(null, channel);
				} else {
					key = this.packetPlayOutMinecraftKeyConstructor.newInstance(channel);
				}

				this.sendCustomPayloadMethod.invoke(player, key, data);
				return;
			}

			// Newer MC version, setup ByteBuf object
			if (this.packetDataSerializerClass != null) {
				if (this.usePacketPayload) {
					Object payload;

					if (this.useDiscardedPayload) {
						if (this.packetPlayOutMinecraftKeyConstructor == null) {
							// 1.21+
							payload = this.discardedPayloadConstructor.newInstance(
								this.resourceLocationParseMethod.invoke(null, channel),
								this.wrappedBufferMethod.invoke(null, data)
							);
						} else {
							// 1.20.5+
							payload = this.discardedPayloadConstructor.newInstance(
								this.packetPlayOutMinecraftKeyConstructor.newInstance(channel),
								this.wrappedBufferMethod.invoke(null, data)
							);
						}
					} else if (this.useDiscardedPayloadByteArray) {
						// 1.21+
						payload = this.discardedPayloadConstructor.newInstance(
							this.resourceLocationParseMethod.invoke(null, channel),
							data
						);
					} else {
						// 1.20.2 - 1.20.4
						payload = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{this.customPacketPayloadClass}, (proxy, method, args) -> {
							if (method.getReturnType().equals(BukkitPluginMessageSender.this.minecraftKeyClass)) {
								return this.packetPlayOutMinecraftKeyConstructor.newInstance(channel);
							} else if (args.length == 1 && this.packetDataSerializerClass.isAssignableFrom(args[0].getClass())) {
								this.packetDataSerializerWriteBytesMethod.invoke(args[0], data);
								return null;
							}

							return null;
						});

					}
					packet = this.packetPlayOutCustomPayloadConstructor.newInstance(payload);
				} else {
					Object byteBuf = this.wrappedBufferMethod.invoke(null, data);
					Object packetDataSerializer = this.packetDataSerializerConstructor.newInstance(byteBuf);

					if (this.useMinecraftKey) {
						Object key = this.packetPlayOutMinecraftKeyConstructor.newInstance(channel);
						packet = this.packetPlayOutCustomPayloadConstructor.newInstance(key, packetDataSerializer);
					} else {
						packet = this.packetPlayOutCustomPayloadConstructor.newInstance(channel, packetDataSerializer);
					}
				}
			} else {
				// Work our magic to make the packet
				packet = this.packetPlayOutCustomPayloadConstructor.newInstance(channel, data);
			}

			// Work our magic to send the packet
			Object nmsPlayer = this.getHandleMethod.invoke(player);
			Object playerConnection = this.playerConnectionField.get(nmsPlayer);
			this.sendPacketMethod.invoke(playerConnection, packet);

		} catch (Throwable throwable) {
			this.apiBukkit.getLogger().log(Level.SEVERE, "Failed to send BLC mod packet", throwable);
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
