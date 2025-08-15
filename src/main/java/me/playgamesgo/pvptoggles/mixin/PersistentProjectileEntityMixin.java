package me.playgamesgo.pvptoggles.mixin;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
    @Shadow private @Nullable IntOpenHashSet piercedEntities;

    public PersistentProjectileEntityMixin(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canHit(Lnet/minecraft/entity/Entity;)Z", at = @At("HEAD"), cancellable = true)
    private void canHitMixin(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean canHit = entity instanceof PlayerEntity player && this.getOwner() instanceof PlayerEntity attacker && !player.shouldDamagePlayer(attacker)
                ? false
                : super.canHit(entity) && (this.piercedEntities == null || !this.piercedEntities.contains(entity.getId()));
        cir.setReturnValue(canHit);
        cir.cancel();
    }
}
