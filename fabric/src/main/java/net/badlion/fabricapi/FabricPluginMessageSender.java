package net.badlion.fabricapi;

import net.badlion.modapicommon.AbstractPluginMessageSender;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

public class FabricPluginMessageSender extends AbstractPluginMessageSender {

    private static final Logger LOGGER = LogManager.getLogger(FabricPluginMessageSender.class);
    private final FabricBadlionPlugin plugin;
    private final MinecraftServer server;

    public FabricPluginMessageSender(FabricBadlionPlugin fabricBadlionPlugin, MinecraftServer server) {
        this.plugin = fabricBadlionPlugin;
        this.server = server;
    }

    @Override
    public void sendPluginMessage(byte[] data) {
        MinecraftServer server = this.server;
        if (server == null) {
            LOGGER.error("Badlion Mod api Failed to get server");
            return;
        }

        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeBytes(data);

        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
            this.sendPluginMessage(serverPlayerEntity, packetByteBuf);
        }
    }

    private void sendPluginMessage(ServerPlayerEntity player, PacketByteBuf packetByteBuf) {
        ServerPlayNetworking.send(player, this.plugin.identifier(), packetByteBuf);
    }


    @Override
    public void sendPluginMessage(UUID uuid, byte[] data) {
        MinecraftServer server = this.server;
        if (server == null) {
            LOGGER.error("Badlion Mod api Failed to get server");
            return;
        }

        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);

        if (player == null) {
            return;
        }

        PacketByteBuf packetByteBuf = PacketByteBufs.create();
        packetByteBuf.writeBytes(data);

        ServerPlayNetworking.send(player, this.plugin.identifier(), packetByteBuf);
    }
}
