package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.events.CombatCallback;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.packets.CombatPacket;
import me.playgamesgo.pvptoggles.packets.TogglePVP;
import me.playgamesgo.pvptoggles.utils.CompatUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

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
    @Unique private boolean PVPToggles$hasClientMod = false;

    @Inject(method = "shouldDamagePlayer", at = @At("HEAD"), cancellable = true)
    private void checkPVPMode(PlayerEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();
        PlayerEntity thisEntity = (PlayerEntity) (Object) this;
        if (attacker.equals(thisEntity)) return;

        IPVPEntity attackerPVP = (IPVPEntity) attacker;
        if (!attackerPVP.PVPToggles$isPVPEnabled()) {
            cir.setReturnValue(false);
            if (attackerPVP instanceof Audience audience) {
                audience.sendActionBar(MiniMessage.miniMessage().deserialize(config.getPvpDisabledSelfMessage()));
            }
            return;
        }

        if (!PVPToggles$PVPEnabled) {
            cir.setReturnValue(false);
            if (attackerPVP instanceof Audience audience) {
                audience.sendActionBar(MiniMessage.miniMessage().deserialize(config.getPvpDisabledOtherMessage()));
            }
            return;
        }

        if (config.isEnableCombatManager()) {
            PVPToggles$startCombat();
            attackerPVP.PVPToggles$startCombat();
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
                CombatCallback.EVENT.invoker().onCombatChange((PlayerEntity) (Object) this, false);

                PlayerEntity player = (PlayerEntity) (Object) this;
                if (player instanceof ServerPlayerEntity serverPlayer) ServerPlayNetworking.send(serverPlayer, new CombatPacket(false));
            }
        }
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void loadPVPMode(ReadView view, CallbackInfo ci) {
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            PVPToggles$setPVPEnabled(view.getBoolean("PVPTogglesPVPEnabled", me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled()));
        }
    }

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void writePVPMode(WriteView view, CallbackInfo ci) {
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

            PlayerEntity player = (PlayerEntity) (Object) this;
            CompatUtil.fabricCustomNamesCompat(player);
        }

        if (PVPToggles$hasClientMod) {
            ServerPlayNetworking.send((ServerPlayerEntity) (Object) this, new TogglePVP(PVPToggles$PVPEnabled));
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
        CombatCallback.EVENT.invoker().onCombatChange((PlayerEntity) (Object) this, true);

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity serverPlayer) ServerPlayNetworking.send(serverPlayer, new CombatPacket(true));
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

    @Override
    public boolean PVPToggles$hasClientMod() {
        return this.PVPToggles$hasClientMod;
    }

    @Override
    public void PVPToggles$setHasClientMod(boolean hasClientMod) {
        this.PVPToggles$hasClientMod = hasClientMod;
    }
}
