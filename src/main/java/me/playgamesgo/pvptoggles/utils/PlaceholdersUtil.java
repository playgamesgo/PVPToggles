package me.playgamesgo.pvptoggles.utils;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class PlaceholdersUtil {
    public static void init() {
        Placeholders.register(Identifier.of(PVPToggles.MOD_ID, "pvpstatus"), (ctx, arg) -> parsePlaceholder(ctx, false));
        Placeholders.register(Identifier.of(PVPToggles.MOD_ID, "pvpstatus_raw"), (ctx, arg) -> parsePlaceholder(ctx, true));
    }

    private static PlaceholderResult parsePlaceholder(PlaceholderContext ctx, boolean raw) {
        if (!ctx.hasPlayer()) return PlaceholderResult.invalid("No player!");

        Config config = Config.HANDLER.instance();
        ServerPlayerEntity player = ctx.player();
        IPVPEntity pvpEntity = (IPVPEntity) player;

        if (raw) {
            PlaceholderResult.value(pvpEntity.PVPToggles$isPVPEnabled()
                    ? PVPToggles.getAdventure().asNative(MiniMessage.miniMessage().deserialize(config.getPvpEnabledPlaceholder()))
                    : PVPToggles.getAdventure().asNative(MiniMessage.miniMessage().deserialize(config.getPvpDisabledPlaceholder())));
        }
        return PlaceholderResult.value(pvpEntity.PVPToggles$isPVPEnabled()
                ? config.getPvpEnabledPlaceholder()
                : config.getPvpDisabledPlaceholder());
    }
}
