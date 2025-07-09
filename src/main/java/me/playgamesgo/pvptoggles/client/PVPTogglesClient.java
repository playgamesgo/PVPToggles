package me.playgamesgo.pvptoggles.client;

import me.playgamesgo.pvptoggles.client.config.ClientConfig;
import me.playgamesgo.pvptoggles.utils.PacketRegistry;
import net.fabricmc.api.ClientModInitializer;

public final class PVPTogglesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.HANDLER.load();
        PacketRegistry.registerPackets();
        PacketRegistry.registerS2CPacketListeners();
    }
}
