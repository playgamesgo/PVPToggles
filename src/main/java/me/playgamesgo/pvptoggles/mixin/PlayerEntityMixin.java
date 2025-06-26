package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if =1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
 *///?}

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPVPEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private boolean PVPToggles$PVPEnabled = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled();

    @Inject(method = "shouldDamagePlayer", at = @At("HEAD"), cancellable = true)
    private void checkPVPMode(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();
        IPVPEntity pvp = (IPVPEntity) player;
        if (!pvp.PVPToggles$isPVPEnabled()) {
            cir.setReturnValue(false);
            if (player instanceof Audience audience) {
                audience.sendActionBar(MiniMessage.miniMessage().deserialize(config.getPvpDisabledSelfMessage()));
            }
            return;
        }

        if (!PVPToggles$PVPEnabled) {
            cir.setReturnValue(false);
            if (player instanceof Audience audience) {
                audience.sendActionBar(MiniMessage.miniMessage().deserialize(config.getPvpDisabledOtherMessage()));
            }
            return;
        }
    }

    //? if =1.21.6 {
    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void loadPVPMode(ReadView view, CallbackInfo ci) {
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            PVPToggles$setPVPEnabled(view.getBoolean("PVPTogglesPVPEnabled", me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled()));
        }
    }
    //?} else {
    /*@Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void loadPVPMode(NbtCompound view, CallbackInfo ci) {
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            PVPToggles$setPVPEnabled(view.getBoolean("PVPTogglesPVPEnabled"));
        }
    }
    *///?}

    //? if =1.21.6 {
    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void writePVPMode(WriteView view, CallbackInfo ci) {
        //?} else {
    /*@Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void writePVPMode(NbtCompound view, CallbackInfo ci) {
        *///?}
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            view.putBoolean("PVPTogglesPVPEnabled", PVPToggles$PVPEnabled);
        }
    }

    @Override
    public boolean PVPToggles$isPVPEnabled() {
        return PVPToggles$PVPEnabled;
    }

    @Override
    public void PVPToggles$setPVPEnabled(boolean enabled) {
        this.PVPToggles$PVPEnabled = enabled;
    }
}
