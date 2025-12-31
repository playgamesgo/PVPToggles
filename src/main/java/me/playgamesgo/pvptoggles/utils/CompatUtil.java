package me.playgamesgo.pvptoggles.utils;

import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixin.PlayerNameManagerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.eclipseisoffline.eclipsescustomname.CustomName;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;

public final class CompatUtil {
    public static void fabricCustomNamesCompat(PlayerEntity player) {
        Config config = Config.HANDLER.instance();

        if (config.isAddCompatFabricCustomNames() && PVPToggles.isEclipseCustomNameLoaded) {
            PlayerNameManager playerNameManager = PlayerNameManager.getPlayerNameManager(player.getEntityWorld().getServer(), CustomName.getConfig());
            PlayerNameManagerAccessor accessor = (PlayerNameManagerAccessor) playerNameManager;


            if (player instanceof ServerPlayerEntity serverPlayer) accessor.PVPToggles$markDirty(serverPlayer);
        }
    }
}
