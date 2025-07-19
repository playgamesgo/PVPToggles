package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PVPHandshakePacket(boolean pvpEnabled) implements CustomPayload, ICustomPacket<PVPHandshakePacket> {
    public static final CustomPayload.Id<PVPHandshakePacket> ID = new CustomPayload.Id<>(PVPTogglesConstants.HANDSHAKE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, PVPHandshakePacket> CODEC = PacketCodec.of(PVPHandshakePacket::write, PVPHandshakePacket::new);

    @Override
    public CustomPayload.Id<PVPHandshakePacket> getId() {
        return ID;
    }

    private PVPHandshakePacket(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeBoolean(pvpEnabled);
    }

    @Override
    public void handleClient(PVPHandshakePacket payload, ClientPlayNetworking.Context context) {
        PVPTogglesClient.isPVPEnabled = payload.pvpEnabled;
    }

    @Override
    public void handleServer(PVPHandshakePacket payload, ServerPlayNetworking.Context context) {
        IPVPEntity pvp = (IPVPEntity) context.player();
        pvp.PVPToggles$setHasClientMod(true);
        ServerPlayNetworking.send(context.player(), new PVPHandshakePacket(pvp.PVPToggles$isPVPEnabled()));
    }
}