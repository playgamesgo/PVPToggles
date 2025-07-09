package me.playgamesgo.pvptoggles.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

public interface ICustomPacket<T extends CustomPayload> {
    void handleClient(T payload, ClientPlayNetworking.Context context);
    void handleServer(T payload, ServerPlayNetworking.Context context);
}
