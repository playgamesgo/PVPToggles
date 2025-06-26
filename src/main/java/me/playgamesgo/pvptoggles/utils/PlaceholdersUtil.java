package me.playgamesgo.pvptoggles.utils;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class PlaceholdersUtil {
    public static void init() {
        Placeholders.register(Identifier.of(PVPToggles.MOD_ID, "pvpstatus"), (ctx, arg) -> {
            if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player!");

            Config config = Config.HANDLER.instance();
            ServerPlayerEntity player = ctx.player();
            IPVPEntity pvpEntity = (IPVPEntity) player;

            return PlaceholderResult.value(pvpEntity.PVPToggles$isPVPEnabled()
                ? config.getPvpEnabledPlaceholder()
                : config.getPvpDisabledPlaceholder());
        });

    }
}
