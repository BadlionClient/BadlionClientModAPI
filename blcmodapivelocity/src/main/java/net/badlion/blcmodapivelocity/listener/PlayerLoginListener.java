package net.badlion.blcmodapivelocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import net.badlion.blcmodapivelocity.BlcModApiVelocity;

public class PlayerLoginListener {

    private final BlcModApiVelocity blcModApiVelocity;

    public PlayerLoginListener(BlcModApiVelocity blcModApiVelocity) {
        this.blcModApiVelocity = blcModApiVelocity;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onPLayerLogin(LoginEvent event) {
        Player player = event.getPlayer();

        player.sendPluginMessage(MinecraftChannelIdentifier.create("badlion", "mods"), BlcModApiVelocity.GSON_NON_PRETTY.toJson(this.blcModApiVelocity.getConf().getModsDisallowed()).getBytes());
    }

}
