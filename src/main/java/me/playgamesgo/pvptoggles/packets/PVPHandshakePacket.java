package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.utils.ClientCapabilities;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.ArrayList;
import java.util.List;

public record PVPHandshakePacket(List<ClientCapabilities.Capabilities> capabilities) implements CustomPayload, ICustomPacket<PVPHandshakePacket> {
    public static final CustomPayload.Id<PVPHandshakePacket> ID = new CustomPayload.Id<>(PVPTogglesConstants.HANDSHAKE_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, PVPHandshakePacket> CODEC = PacketCodec.of(PVPHandshakePacket::write, PVPHandshakePacket::new);

    private PVPHandshakePacket(PacketByteBuf buf) {
        this(buf.readList(buf1 -> buf1.readEnumConstant(ClientCapabilities.Capabilities.class)));
    }

    private void write(PacketByteBuf buf) {
        buf.writeCollection(capabilities, PacketByteBuf::writeEnumConstant);
    }

    @Override
    public CustomPayload.Id<PVPHandshakePacket> getId() {
        return ID;
    }

    @Override
    public void handleClient(PVPHandshakePacket payload, ClientPlayNetworking.Context context) {
        ClientCapabilities.setCapability(payload.capabilities);
    }

    @Override
    public void handleServer(PVPHandshakePacket payload, ServerPlayNetworking.Context context) {
        Config config = Config.HANDLER.instance();
        List<ClientCapabilities.Capabilities> capabilities = new ArrayList<>();

        if (config.isAllowClientHealthTracker()) capabilities.add(ClientCapabilities.Capabilities.HEALTH_TRACKER);

        IPVPEntity pvp = (IPVPEntity) context.player();
        pvp.PVPToggles$setHasClientMod(true);

        ServerPlayNetworking.send(context.player(), new PVPHandshakePacket(capabilities));
    }
}