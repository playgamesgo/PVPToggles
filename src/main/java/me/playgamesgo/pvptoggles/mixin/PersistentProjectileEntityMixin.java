package me.playgamesgo.pvptoggles.mixin;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractArrow.class)
public abstract class PersistentProjectileEntityMixin extends Projectile {
    @Shadow private @Nullable IntOpenHashSet piercingIgnoreEntityIds;

    public PersistentProjectileEntityMixin(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }

    @Inject(method = "canHitEntity", at = @At("HEAD"), cancellable = true)
    private void canHitMixin(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean canHit = entity instanceof Player player && this.getOwner() instanceof Player attacker && !player.canHarmPlayer(attacker)
                ? false
                : super.canHitEntity(entity) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(entity.getId()));
        cir.setReturnValue(canHit);
        cir.cancel();
    }
}
