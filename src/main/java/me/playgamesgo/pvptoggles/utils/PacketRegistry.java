package me.playgamesgo.pvptoggles.utils;

import me.playgamesgo.pvptoggles.packets.CombatPacket;
import me.playgamesgo.pvptoggles.packets.ICustomPacket;
import me.playgamesgo.pvptoggles.packets.PVPHandshakePacket;
import me.playgamesgo.pvptoggles.packets.TogglePVP;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.List;

public final class PacketRegistry {
    private static final List<Class<? extends ICustomPacket<? extends CustomPayload>>> clientPackets = List.of(
            PVPHandshakePacket.class,
            TogglePVP.class
    );

    private static final List<Class<? extends ICustomPacket<? extends CustomPayload>>> serverPackets = List.of(
            PVPHandshakePacket.class,
            CombatPacket.class,
            TogglePVP.class
    );

    public static void registerPackets() {
        clientPackets.forEach(PacketRegistry::registerC2SPacket);
        serverPackets.forEach(PacketRegistry::registerS2CPacket);
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPayload> void registerC2SPacket(Class<? extends ICustomPacket<? extends CustomPayload>> packetClass) {
        try {
            CustomPayload.Id<T> id = (CustomPayload.Id<T>) packetClass.getDeclaredField("ID").get(null);
            PacketCodec<RegistryByteBuf, T> codec = (PacketCodec<RegistryByteBuf, T>) packetClass.getDeclaredField("CODEC").get(null);

            PayloadTypeRegistry.playC2S().register(id, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + packetClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends CustomPayload> void registerS2CPacket(Class<? extends ICustomPacket<? extends CustomPayload>> packetClass) {
        try {
            CustomPayload.Id<T> id = (CustomPayload.Id<T>) packetClass.getDeclaredField("ID").get(null);
            PacketCodec<RegistryByteBuf, T> codec = (PacketCodec<RegistryByteBuf, T>) packetClass.getDeclaredField("CODEC").get(null);

            PayloadTypeRegistry.playS2C().register(id, codec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to register packet: " + packetClass.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPayload> void registerC2SPacketListeners() {
        clientPackets.forEach(packetClass -> {
            try {
                CustomPayload.Id<T> id = (CustomPayload.Id<T>) packetClass.getDeclaredField("ID").get(null);
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
    public static <T extends CustomPayload> void registerS2CPacketListeners() {
        serverPackets.forEach(packetClass -> {
            try {
                CustomPayload.Id<T> id = (CustomPayload.Id<T>) packetClass.getDeclaredField("ID").get(null);
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