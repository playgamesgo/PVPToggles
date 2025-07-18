package me.playgamesgo.pvptoggles.packets;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;

public record TogglePVP(boolean enable) implements CustomPayload, ICustomPacket<TogglePVP> {
    public static final CustomPayload.Id<TogglePVP> ID = new CustomPayload.Id<>(PVPTogglesConstants.TOGGLE_PVP_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, TogglePVP> CODEC = PacketCodec.of(TogglePVP::write, TogglePVP::new);

    @Override
    public CustomPayload.Id<TogglePVP> getId() {
        return ID;
    }

    private TogglePVP(RegistryByteBuf buf) {
        this(buf.readBoolean());
    }

    private void write(RegistryByteBuf buf) {
        buf.writeBoolean(enable);
    }

    @Override
    public void handleClient(TogglePVP payload, ClientPlayNetworking.Context context) {
        context.client().player.sendMessage(Component.text(payload.enable));
    }

    @Override
    public void handleServer(TogglePVP payload, ServerPlayNetworking.Context context) {
        Config config = Config.HANDLER.instance();
        ServerPlayerEntity player = context.player();
        IPVPEntity pvp = (IPVPEntity) player;

        if (pvp.PVPToggles$isInCombat()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getCantTogglePVPInCombatMessage()));
            return;
        }

        if (pvp.PVPToggles$isDisablePVPAfterDelay()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpAlreadyDisablingMessage()));
            return;
        }

        if (!pvp.PVPToggles$isPVPEnabled()) pvp.PVPToggles$setPVPEnabled(true);
        else pvp.PVPToggles$setDisablePVPAfterDelay();
    }
}
