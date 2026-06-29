package me.playgamesgo.pvptoggles.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public interface ICustomPacket<T extends CustomPacketPayload> {
    void handleClient(T payload, ClientPlayNetworking.Context context);
    void handleServer(T payload, ServerPlayNetworking.Context context);
}
