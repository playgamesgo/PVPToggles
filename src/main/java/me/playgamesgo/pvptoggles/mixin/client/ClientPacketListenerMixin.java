package me.playgamesgo.pvptoggles.mixin.client;

import me.playgamesgo.pvptoggles.packets.PVPHandshakePacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void onGameJoin(CallbackInfo info) {
        ClientPlayNetworking.send(new PVPHandshakePacket(false));
    }
}
