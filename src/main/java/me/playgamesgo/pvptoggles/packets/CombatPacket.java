package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

public record CombatPacket(boolean start) implements CustomPacketPayload, ICustomPacket<CombatPacket> {
    public static final CustomPacketPayload.Type<CombatPacket> ID = new CustomPacketPayload.Type<>(PVPTogglesConstants.COMBAT_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, CombatPacket> CODEC = StreamCodec.ofMember(CombatPacket::write, CombatPacket::new);

    private CombatPacket(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(start);
    }

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
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
