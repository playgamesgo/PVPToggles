package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PVPHandshakePacket() implements CustomPayload, ICustomPacket<PVPHandshakePacket> {
    public static final CustomPayload.Id<PVPHandshakePacket> ID = new CustomPayload.Id<>(PVPTogglesConstants.HANDSHAKE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, PVPHandshakePacket> CODEC = PacketCodec.unit(new PVPHandshakePacket());

    @Override
    public CustomPayload.Id<PVPHandshakePacket> getId() {
        return ID;
    }

    @Override
    public void handleClient(PVPHandshakePacket payload, ClientPlayNetworking.Context context) {
        throw new UnsupportedOperationException("PVPHandshakePacket should not be handled on the client side");
    }

    @Override
    public void handleServer(PVPHandshakePacket payload, ServerPlayNetworking.Context context) {
        IPVPEntity pvp = (IPVPEntity) context.player();
        pvp.PVPToggles$setHasClientMod(true);
    }
}