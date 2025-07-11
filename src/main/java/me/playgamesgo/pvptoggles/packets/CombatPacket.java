package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record CombatPacket(boolean start) implements CustomPayload, ICustomPacket<CombatPacket> {
    public static final CustomPayload.Id<CombatPacket> ID = new CustomPayload.Id<>(PVPTogglesConstants.COMBAT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, CombatPacket> CODEC = PacketCodec.of(CombatPacket::write, CombatPacket::new);

    private CombatPacket(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeBoolean(start);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public void handleClient(CombatPacket payload, ClientPlayNetworking.Context context) {
        PVPTogglesClient.isInCombat = payload.start;
    }

    @Override
    public void handleServer(CombatPacket payload, ServerPlayNetworking.Context context) {
        throw new UnsupportedOperationException("CombatPacket should not be handled on the server side");
    }
}
