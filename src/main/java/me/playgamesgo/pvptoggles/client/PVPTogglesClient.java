package me.playgamesgo.pvptoggles.client;

import me.playgamesgo.pvptoggles.client.config.ClientConfig;
import me.playgamesgo.pvptoggles.client.gui.PVPIndicator;
import me.playgamesgo.pvptoggles.client.utils.Keybinds;
import me.playgamesgo.pvptoggles.utils.PacketRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class PVPTogglesClient implements ClientModInitializer {
    public static boolean isInCombat = false;
    public static boolean isPVPEnabled = false;

    @Override
    public void onInitializeClient() {
        ClientConfig.HANDLER.load();
        PacketRegistry.registerPackets();
        PacketRegistry.registerS2CPacketListeners();
        Keybinds.init();
        PVPIndicator.init();
    }
}
