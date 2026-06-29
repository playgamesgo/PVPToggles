package me.playgamesgo.pvptoggles.mixin;

import com.mojang.datafixers.util.Pair;
import me.playgamesgo.pvptoggles.events.CombatCallback;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.packets.CombatPacket;
import me.playgamesgo.pvptoggles.packets.TogglePVP;
import me.playgamesgo.pvptoggles.utils.CompatUtil;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.*;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements IPVPEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Unique private boolean PVPToggles$PVPEnabled = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled();
    @Unique private boolean PVPToggles$DisablePVPAfterDelay = false;
    @Unique private int PVPToggles$PvpDelayTimer = 0;
    @Unique private int PVPToggles$CombatTimer = 0;
    @Unique private BossBar PVPToggles$lastBossBar = null;
    @Unique private boolean PVPToggles$hasClientMod = false;

    @Unique private final Map<UUID, Pair<Long, Float>> PVPToggles$accumulatedDamageSources = new HashMap<>(); // UUID -> (timestamp, accumulated damage)

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    private void checkPVPMode(Player target, CallbackInfoReturnable<Boolean> cir) {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();
        Player thisEntity = (Player) (Object) this;
        if (target.equals(thisEntity)) return;

        IPVPEntity attackerPVP = (IPVPEntity) target;
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
        }
    }

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Avatar;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private void onDamageTaken(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();

        if (source.getEntity() instanceof Player attacker) {
            IPVPEntity attackerPVP = (IPVPEntity) attacker;

            if (config.isEnableCombatManager()) {
                if (!config.isCombatManagerAccumulateDamageTrigger()) {
                    if (config.getCombatManagerMinDamageToTrigger() <= damage) {
                        PVPToggles$startCombat();
                        attackerPVP.PVPToggles$startCombat();
                    }
                } else {
                    UUID attackerUUID = attacker.getUUID();
                    long currentTime = System.currentTimeMillis();

                    Map<UUID, Pair<Long, Float>> accumulatedDamageSourcesCopy = new HashMap<>(PVPToggles$accumulatedDamageSources);
                    for (Map.Entry<UUID, Pair<Long, Float>> entry : accumulatedDamageSourcesCopy.entrySet()) {
                        if (currentTime - entry.getValue().getFirst() > config.getCombatManagerDamageAccumulationTimeframeSeconds() * 1000L) {
                            PVPToggles$accumulatedDamageSources.remove(entry.getKey());
                        }
                    }

                    float totalAccumulatedDamage = damage;
                    if (PVPToggles$accumulatedDamageSources.containsKey(attackerUUID)) {
                        Pair<Long, Float> existingSource = PVPToggles$accumulatedDamageSources.get(attackerUUID);
                        totalAccumulatedDamage += existingSource.getSecond();
                    }

                    PVPToggles$accumulatedDamageSources.put(attackerUUID, new Pair<>(currentTime, totalAccumulatedDamage));

                    if (totalAccumulatedDamage >= config.getCombatManagerAccumulateDamageThreshold()) {
                        PVPToggles$startCombat();
                        attackerPVP.PVPToggles$startCombat();
                        PVPToggles$accumulatedDamageSources.remove(attackerUUID);
                    }
                }
            }
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
                    PVPToggles$setPVPEnabled(false, false);
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
                CombatCallback.EVENT.invoker().onCombatChange((Player) (Object) this, false);

                Player player = (Player) (Object) this;
                if (player instanceof ServerPlayer serverPlayer)
                    ServerPlayNetworking.send(serverPlayer, new CombatPacket(false));
            }
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void loadPVPMode(ValueInput input, CallbackInfo ci) {
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            PVPToggles$setPVPEnabled(input.getBooleanOr("PVPTogglesPVPEnabled", me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isDefaultPVPEnabled()), true);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void writePVPMode(ValueOutput output, CallbackInfo ci) {
        if (me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isSaveInPlayerData()) {
            output.putBoolean("PVPTogglesPVPEnabled", PVPToggles$PVPEnabled);
        }
    }

    @Override
    public boolean PVPToggles$isPVPEnabled() {
        return this.PVPToggles$PVPEnabled;
    }

    @Override
    public void PVPToggles$setPVPEnabled(boolean enabled, boolean silent) {
        this.PVPToggles$PVPEnabled = enabled;

        me.playgamesgo.pvptoggles.utils.Config config = me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance();
        if (!silent && this instanceof Audience audience) {
            if (enabled) audience.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpEnabledMessage()));
            else audience.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpDisabledMessage()));
        }

        Player player = (Player) (Object) this;
        CompatUtil.fabricCustomNamesCompat(player);

        if (PVPToggles$hasClientMod) {
            ServerPlayNetworking.send((ServerPlayer) (Object) this, new TogglePVP(PVPToggles$PVPEnabled));
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
        CombatCallback.EVENT.invoker().onCombatChange((Player) (Object) this, true);

        Player player = (Player) (Object) this;
        if (player instanceof ServerPlayer serverPlayer)
            ServerPlayNetworking.send(serverPlayer, new CombatPacket(true));
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
    public void PVPToggles$setDisablePVPAfterDelay(boolean enabled) {
        this.PVPToggles$DisablePVPAfterDelay = enabled;
    }

    @Override
    public void PVPToggles$setPVPDelayTimer(int ticks) {
        this.PVPToggles$PvpDelayTimer = ticks;
    }

    @Override
    public int PVPToggles$getPvpDelayTimer() {
        return this.PVPToggles$PvpDelayTimer;
    }

    @Override
    public boolean PVPToggles$isInCombat() {
        return this.PVPToggles$CombatTimer > 0;
    }

    @Override
    public void PVPToggles$resetCombatTimer() {
        this.PVPToggles$CombatTimer = 0;
        CombatCallback.EVENT.invoker().onCombatChange((Player) (Object) this, false);
        if (this instanceof Audience audience && me.playgamesgo.pvptoggles.utils.Config.HANDLER.instance().isCombatManagerShowBossBar()) {
            if (PVPToggles$lastBossBar != null) audience.hideBossBar(PVPToggles$lastBossBar);
            PVPToggles$lastBossBar = null;
        }
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
