package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record PVPHandshakePacket(boolean pvpEnabled) implements CustomPacketPayload, ICustomPacket<PVPHandshakePacket> {
    public static final CustomPacketPayload.Type<PVPHandshakePacket> ID = new CustomPacketPayload.Type<>(PVPTogglesConstants.HANDSHAKE_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PVPHandshakePacket> CODEC = StreamCodec.ofMember(PVPHandshakePacket::write, PVPHandshakePacket::new);

    @Override
    public CustomPacketPayload.@NonNull Type<PVPHandshakePacket> type() {
        return ID;
    }

    private PVPHandshakePacket(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryFriendlyByteBuf buf) {
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