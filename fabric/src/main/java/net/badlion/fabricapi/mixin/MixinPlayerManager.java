package net.badlion.fabricapi.mixin;

import net.badlion.fabricapi.events.PlayerJoinEvent;
import net.badlion.fabricapi.events.PlayerLeaveEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public abstract class MixinPlayerManager {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerJoinEvent.EVENT.invoker().onPlayerJoin(this.getServer(), player);
    }

    @Inject(at = @At("HEAD"), method = "remove")
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerLeaveEvent.EVENT.invoker().onPlayerLeave(this.getServer(), player);
    }

}
