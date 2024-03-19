package net.badlion.fabricapi.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerLeaveEvent {
    Event<PlayerLeaveEvent> EVENT = EventFactory.createArrayBacked(PlayerLeaveEvent.class,
        (listeners) -> (server, player) -> {
            for (PlayerLeaveEvent event : listeners) {
                event.onPlayerLeave(server, player);
            }
        }
    );

    void onPlayerLeave(MinecraftServer server, ServerPlayerEntity player);
}
