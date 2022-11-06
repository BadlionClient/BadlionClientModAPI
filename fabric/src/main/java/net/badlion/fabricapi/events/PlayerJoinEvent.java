package net.badlion.fabricapi.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerJoinEvent {
    Event<PlayerJoinEvent> EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class,
        (listeners) -> (server, player) -> {
            for (PlayerJoinEvent event : listeners) {
                event.onPlayerJoin(server, player);
            }
        }
    );

    void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player);
}
