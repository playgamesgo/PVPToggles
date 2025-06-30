package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if =1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import xyz.eclipseisoffline.eclipsescustomname.CustomName;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;
//?} else {
/*import net.minecraft.nbt.NbtCompound;
 *///?}

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements IPVPEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Unique private boolean PVPToggles$PVPEnabled = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled();
    @Unique private boolean PVPToggles$DisablePVPAfterDelay = false;
    @Unique private int PVPToggles$PvpDelayTimer = 0;
    @Unique private int PVPToggles$CombatTimer = 0;
    @Unique private BossBar PVPToggles$lastBossBar = null;

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

        if (config.isEnableCombatManager()) {
            PVPToggles$startCombat();
            pvp.PVPToggles$startCombat();
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickPVPMode(CallbackInfo ci) {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();

        if (PVPToggles$PvpDelayTimer > 0) {
            PVPToggles$PvpDelayTimer--;
            if (PVPToggles$PvpDelayTimer % 20 == 0) {
                int secondsLeft = PVPToggles$PvpDelayTimer / 20;
                if (secondsLeft > 0) {
                    if (this instanceof Audience audience) {
                        audience.sendActionBar(MiniMessage.miniMessage().deserialize(
                                config.getPvpDisableDelayMessage().replace("{timer}", secondsLeft + "")));
                    }
                } else {
                    PVPToggles$DisablePVPAfterDelay = false;
                    PVPToggles$setPVPEnabled(false);
                }
            }
        }

        if (PVPToggles$CombatTimer > 0) {
            PVPToggles$CombatTimer--;
            if (this instanceof Audience audience && config.isCombatManagerShowBossBar()) {
                if (PVPToggles$lastBossBar != null) audience.hideBossBar(PVPToggles$lastBossBar);
                BossBar bossBar = BossBar.bossBar(
                        MiniMessage.miniMessage().deserialize(config.getCombatBossBarTitle()
                                .replace("{timer}", String.valueOf(PVPToggles$CombatTimer / 20))),
                        (float) PVPToggles$CombatTimer / (config.getCombatManagerPVPDurationSeconds() * 20),
                        config.getCombatBossBarColor(), BossBar.Overlay.PROGRESS
                );
                audience.showBossBar(bossBar);
                PVPToggles$lastBossBar = bossBar;
            }

            if (PVPToggles$CombatTimer <= 0) {
                if (this instanceof Audience audience && config.isCombatManagerShowBossBar()) {
                    audience.hideBossBar(PVPToggles$lastBossBar);
                }
                PVPToggles$lastBossBar = null;
            }
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
        return this.PVPToggles$PVPEnabled;
    }

    @Override
    public void PVPToggles$setPVPEnabled(boolean enabled) {
        this.PVPToggles$PVPEnabled = enabled;

        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();
        if (this instanceof Audience audience) {
            if (enabled) audience.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpEnabledMessage()));
            else audience.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpDisabledMessage()));


            if (config.isAddCompatFabricCustomNames() && PVPToggles.isEclipseCustomNameLoaded) {
                //? if >1.20.4 {
                PlayerNameManager playerNameManager = PlayerNameManager.getPlayerNameManager(getServer(), CustomName.getConfig());
                //?} else {
                /*PlayerNameManager playerNameManager = PlayerNameManager.getPlayerNameManager(getServer());
                 *///?}
                PlayerNameManagerAccessor accessor = (PlayerNameManagerAccessor) playerNameManager;

                PlayerEntity player = (PlayerEntity) (Object) this;
                if (player instanceof ServerPlayerEntity serverPlayer) accessor.PVPToggles$markDirty(serverPlayer);
            }
        }
    }

    @Override
    public void PVPToggles$startCombat() {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();

        PVPToggles$CombatTimer = config.getCombatManagerPVPDurationSeconds() * 20;
        PVPToggles$PvpDelayTimer = 0;
        if (PVPToggles$DisablePVPAfterDelay) {
            if (this instanceof Audience audience) {
                audience.sendActionBar(MiniMessage.miniMessage().deserialize(config.getPvpDisableCancelledMessage()));
            }
            PVPToggles$DisablePVPAfterDelay = false;
        }
    }

    @Override
    public boolean PVPToggles$isDisablePVPAfterDelay() {
        return this.PVPToggles$DisablePVPAfterDelay;
    }

    @Override
    public void PVPToggles$setDisablePVPAfterDelay() {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();

        this.PVPToggles$DisablePVPAfterDelay = true;
        this.PVPToggles$PvpDelayTimer = config.getPvpDisableDelaySeconds() * 20;
        if (this instanceof Audience audience) {
            audience.sendActionBar(MiniMessage.miniMessage().deserialize(
                    config.getPvpDisableDelayMessage()
                            .replace("{timer}", String.valueOf(PVPToggles$PvpDelayTimer / 20))));
        }
    }

    @Override
    public boolean PVPToggles$isInCombat() {
        return this.PVPToggles$CombatTimer > 0;
    }
}
