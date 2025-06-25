package me.playgamesgo.pvptoggles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

public final class PlayerDisplayNameMixins {
    @Mixin(PlayerManager.class)
    public static class PlayerManagerMixin {
        @ModifyArg(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;", ordinal = 0), index = 1)
        private Object[] modifyDisplayName(Object[] args, @Local(argsOnly = true) ServerPlayerEntity player) {
            return new Text[]{((IPVPEntity) player).PVPToggles$getFormatedDisplayName()};
        }
    }

    @Mixin(AdvancementFrame.class)
    public static class AdvancementFrameMixin {
        @ModifyArg(method = "getChatAnnouncementText", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;", ordinal = 0), index = 1)
        private Object[] modifyDisplayName(Object[] args, @Local(argsOnly = true) ServerPlayerEntity player) {
            return new Text[]{((IPVPEntity) player).PVPToggles$getFormatedDisplayName()};
        }
    }
}
