package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "die", at = @At("HEAD"))
    private void onDeathClearTimers(CallbackInfo ci) {
        if (this instanceof IPVPEntity pvp) {
            pvp.PVPToggles$resetCombatTimer();
        }
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyFrom(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (this instanceof IPVPEntity newPVP && oldPlayer instanceof IPVPEntity oldPVP) {
            newPVP.PVPToggles$setPVPEnabled(oldPVP.PVPToggles$isPVPEnabled(), false);
            newPVP.PVPToggles$setDisablePVPAfterDelay(oldPVP.PVPToggles$isDisablePVPAfterDelay());
            newPVP.PVPToggles$setPVPDelayTimer(oldPVP.PVPToggles$getPvpDelayTimer());
            newPVP.PVPToggles$setHasClientMod(oldPVP.PVPToggles$hasClientMod());
        }
    }
}
