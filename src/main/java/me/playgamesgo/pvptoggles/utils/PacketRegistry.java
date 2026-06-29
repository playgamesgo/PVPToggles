package me.playgamesgo.pvptoggles.utils;

import me.playgamesgo.pvptoggles.packets.CombatPacket;
import me.playgamesgo.pvptoggles.packets.ICustomPacket;
import me.playgamesgo.pvptoggles.packets.PVPHandshakePacket;
import me.playgamesgo.pvptoggles.packets.TogglePVP;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public final class PacketRegistry {
    private static final List<Class<? extends ICustomPacket<? extends CustomPacketPayload>>> clientPackets = List.of(
            PVPHandshakePacket.class,
            TogglePVP.class
    );

    private static final List<Class<? extends ICustomPacket<? extends CustomPacketPayload>>> serverPackets = List.of(
            PVPHandshakePacket.class,
            CombatPacket.class,
            TogglePVP.class
    );

    public static void registerPackets() {
        clientPackets.forEach(PacketRegistry::registerC2SPacket);
        serverPackets.forEach(PacketRegistry::registerS2CPacket);
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerC2SPacket(Class<? extends ICustomPacket<? extends CustomPacketPayload>> packetClass) {
        try {
            CustomPacketPayload.Type<T> id = (CustomPacketPayload.Type<T>) packetClass.getDeclaredField("ID").get(null);
            StreamCodec<RegistryFriendlyByteBuf, T> codec = (StreamCodec<RegistryFriendlyByteBuf, T>) packetClass.getDeclaredField("CODEC").get(null);

            PayloadTypeRegistry.serverboundPlay().register(id, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + packetClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPacketPayload> void registerS2CPacket(Class<? extends ICustomPacket<? extends CustomPacketPayload>> packetClass) {
        try {
            CustomPacketPayload.Type<T> id = (CustomPacketPayload.Type<T>) packetClass.getDeclaredField("ID").get(null);
            StreamCodec<RegistryFriendlyByteBuf, T> codec = (StreamCodec<RegistryFriendlyByteBuf, T>) packetClass.getDeclaredField("CODEC").get(null);

            PayloadTypeRegistry.clientboundPlay().register(id, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + packetClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> void registerC2SPacketListeners() {
        clientPackets.forEach(packetClass -> {
            try {
                CustomPacketPayload.Type<T> id = (CustomPacketPayload.Type<T>) packetClass.getDeclaredField("ID").get(null);
                ServerPlayNetworking.registerGlobalReceiver(id, (payload, context) -> {
                    ICustomPacket<T> packet = (ICustomPacket<T>) payload;
                    packet.handleServer(payload, context);
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to register C2S packet listener: " + packetClass.getName(), e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> void registerS2CPacketListeners() {
        serverPackets.forEach(packetClass -> {
            try {
                CustomPacketPayload.Type<T> id = (CustomPacketPayload.Type<T>) packetClass.getDeclaredField("ID").get(null);
                ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> {
                    ICustomPacket<T> packet = (ICustomPacket<T>) payload;
                    packet.handleClient(payload, context);
                });
            } catch (Exception e) {
                throw new RuntimeException("Failed to register S2C packet listener: " + packetClass.getName(), e);
            }
        });
    }
}