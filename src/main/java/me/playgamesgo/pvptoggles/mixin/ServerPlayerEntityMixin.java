package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeathClearTimers(CallbackInfo ci) {
        if (this instanceof IPVPEntity pvp) {
            pvp.PVPToggles$resetCombatTimer();
        }
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (this instanceof IPVPEntity newPVP && oldPlayer instanceof IPVPEntity oldPVP) {
            newPVP.PVPToggles$setPVPEnabled(oldPVP.PVPToggles$isPVPEnabled());
            newPVP.PVPToggles$setDisablePVPAfterDelay(oldPVP.PVPToggles$isDisablePVPAfterDelay());
            newPVP.PVPToggles$setPVPDelayTimer(oldPVP.PVPToggles$getPvpDelayTimer());
            newPVP.PVPToggles$setHasClientMod(oldPVP.PVPToggles$hasClientMod());
        }
    }
}
