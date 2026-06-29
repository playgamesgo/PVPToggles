package me.playgamesgo.pvptoggles.utils;

import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixin.PlayerNameManagerAccessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;

public final class CompatUtil {
    public static void fabricCustomNamesCompat(Player player) {
        Config config = Config.HANDLER.instance();

        if (config.isAddCompatFabricCustomNames() && PVPToggles.isEclipseCustomNameLoaded) {
            PlayerNameManager playerNameManager = PlayerNameManager.getPlayerNameManager(player.level().getServer());
            PlayerNameManagerAccessor accessor = (PlayerNameManagerAccessor) playerNameManager;


            if (player instanceof ServerPlayer serverPlayer) accessor.PVPToggles$markDirty(serverPlayer);
        }
    }
}
