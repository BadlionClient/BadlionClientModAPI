package net.badlion.fabricapi;

import net.badlion.fabricapi.events.PlayerJoinEvent;
import net.badlion.fabricapi.events.PlayerLeaveEvent;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;

public class PlayerListener implements PlayerJoinEvent, PlayerLeaveEvent {
    private final FabricBadlionPlugin plugin;

    public PlayerListener(FabricBadlionPlugin fabricBadlionPlugin) {
        this.plugin = fabricBadlionPlugin;
    }

    @Override
    public void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        if (!Objects.equals(this.plugin.getServer(), server)) {
            return;
        }

        this.plugin.getPluginMessageSender().sendPluginMessage(player.getUuid(), AbstractBadlionApi.GSON_NON_PRETTY.toJson(this.plugin.getBadlionConfig()).getBytes());
        this.plugin.getCosmeticManager().onPlayerJoin(player);
        this.plugin.getWaypointManager().onPlayerJoin(player);
    }

    @Override
    public void onPlayerLeave(MinecraftServer server, ServerPlayerEntity player) {
        if (!Objects.equals(this.plugin.getServer(), server)) {
            return;
        }

        this.plugin.getCosmeticManager().onPlayerLeave(player);
        this.plugin.getWaypointManager().onPlayerLeave(player);
    }
}
