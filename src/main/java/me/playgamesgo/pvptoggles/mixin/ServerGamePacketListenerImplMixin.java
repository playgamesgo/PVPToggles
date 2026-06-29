package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "removePlayerFromWorld", at = @At("HEAD"))
    private void pvptoggles$onCleanUp(CallbackInfo ci) {
        Config config = Config.HANDLER.instance();
        if (!config.isCombatManagerKillOnLogout() || !config.isEnableCombatManager()) return;

        IPVPEntity pvp = (IPVPEntity) player;
        if (pvp.PVPToggles$isInCombat()) {
            player.kill(player.level());
        }
    }
}
