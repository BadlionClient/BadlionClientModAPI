package net.badlion.fabricapi.cosmetics;

import com.google.gson.JsonObject;
import net.badlion.modapicommon.AbstractBadlionApi;
import net.badlion.modapicommon.utility.AbstractCosmeticManager;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticManager extends AbstractCosmeticManager {

    private final Set<UUID> disabledNametags = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public CosmeticManager() {
    }

    @Override
    public void disableNametagCosmetics(UUID uuid) {
        if (!this.disabledNametags.add(uuid)) {
            return;
        }

        JsonObject data = this.getDisabledCosmeticsData(uuid, true, "nametag");

        AbstractBadlionApi.getInstance().getPluginMessageSender().sendData("cosmetics", data);
    }

    @Override
    public void enableNametagCosmetics(UUID uuid) {
        if (!this.disabledNametags.remove(uuid)) {
            return;
        }

        JsonObject data = this.getDisabledCosmeticsData(uuid, false, "nametag");

        AbstractBadlionApi.getInstance().getPluginMessageSender().sendData("cosmetics", data);
    }

    public void onPlayerLeave(ServerPlayerEntity player) {
        this.enableNametagCosmetics(player.getUuid());
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        if (!this.disabledNametags.isEmpty()) {
            for (UUID uuid : this.disabledNametags) {
                JsonObject data = this.getDisabledCosmeticsData(uuid, false, "nametag");

                AbstractBadlionApi.getInstance().getPluginMessageSender().sendData(player.getUuid(), "cosmetics", data);
            }
        }
    }
}
