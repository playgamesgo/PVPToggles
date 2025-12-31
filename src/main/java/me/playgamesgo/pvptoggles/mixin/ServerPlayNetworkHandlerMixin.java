package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "cleanUp", at = @At("HEAD"))
    private void pvptoggles$onCleanUp(CallbackInfo ci) {
        Config config = Config.HANDLER.instance();
        if (!config.isCombatManagerKillOnLogout() || !config.isEnableCombatManager()) return;

        IPVPEntity pvp = (IPVPEntity) player;
        if (pvp.PVPToggles$isInCombat()) {
            player.kill(player.getEntityWorld());
        }
    }
}
