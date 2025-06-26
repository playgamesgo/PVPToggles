package me.playgamesgo.pvptoggles.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;

@Pseudo
@Mixin(value = PlayerNameManager.class, remap = false)
public class PlayerNameManagerMixin {
    @Inject(method = "updateFullPlayerName", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private void includePVPStatus(ServerPlayerEntity player, CallbackInfo ci, @Local MutableText name) {
        Config config = Config.HANDLER.instance();
        if (!config.isAddCompatFabricCustomNames()) return;

        IPVPEntity pvpEntity = (IPVPEntity) player;
        name.append(
                pvpEntity.PVPToggles$isPVPEnabled()
                        ? config.getPvpEnabledPlaceholder()
                        : config.getPvpDisabledPlaceholder()
        );
    }
}
