package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.NonNull;

public record TogglePVP(boolean enable) implements CustomPacketPayload, ICustomPacket<TogglePVP> {
    public static final CustomPacketPayload.Type<TogglePVP> ID = new CustomPacketPayload.Type<>(PVPTogglesConstants.TOGGLE_PVP_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePVP> CODEC = StreamCodec.ofMember(TogglePVP::write, TogglePVP::new);

    @Override
    public CustomPacketPayload.@NonNull Type<TogglePVP> type() {
        return ID;
    }

    private TogglePVP(RegistryFriendlyByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryFriendlyByteBuf buf) {
        buf.writeBoolean(enable);
    }

    @Override
    public void handleClient(TogglePVP payload, ClientPlayNetworking.Context context) {
        PVPTogglesClient.isPVPEnabled = payload.enable;
    }

    @Override
    public void handleServer(TogglePVP payload, ServerPlayNetworking.Context context) {
        Config config = Config.HANDLER.instance();
        ServerPlayer player = context.player();
        IPVPEntity pvp = (IPVPEntity) player;

        if (pvp.PVPToggles$isInCombat()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getCantTogglePVPInCombatMessage()));
            return;
        }

        if (pvp.PVPToggles$isDisablePVPAfterDelay()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpAlreadyDisablingMessage()));
            return;
        }

        if (!pvp.PVPToggles$isPVPEnabled()) pvp.PVPToggles$setPVPEnabled(true, false);
        else pvp.PVPToggles$setDisablePVPAfterDelay();
    }
}
