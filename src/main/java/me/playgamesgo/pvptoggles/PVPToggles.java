package me.playgamesgo.pvptoggles;

import me.playgamesgo.pvptoggles.commands.PVPToggleCommand;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PacketRegistry;
import me.playgamesgo.pvptoggles.utils.PlaceholdersUtil;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class PVPToggles implements DedicatedServerModInitializer {
    public static final String MOD_ID = "pvptoggles";
    public static final boolean isEclipseCustomNameLoaded = FabricLoader.getInstance().isModLoaded("eclipsescustomname");

    @Override
    public void onInitializeServer() {
        Config.HANDLER.load();
        PVPToggleCommand.init();
        PlaceholdersUtil.init();
        PacketRegistry.registerPackets();
        PacketRegistry.registerC2SPacketListeners();
    }
}
