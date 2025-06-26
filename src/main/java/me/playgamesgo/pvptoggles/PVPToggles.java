package me.playgamesgo.pvptoggles;

import me.playgamesgo.pvptoggles.commands.PVPToggleCommand;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PlaceholdersUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class PVPToggles implements ModInitializer {
    public static final String MOD_ID = "pvptoggles";
    public static final boolean isEclipseCustomNameLoaded = FabricLoader.getInstance().isModLoaded("eclipsescustomname");

    @Override
    public void onInitialize() {
        Config.HANDLER.load();
        PVPToggleCommand.init();
        PlaceholdersUtil.init();
    }
}
