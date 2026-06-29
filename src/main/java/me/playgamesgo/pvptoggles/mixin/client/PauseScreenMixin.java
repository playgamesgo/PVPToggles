package me.playgamesgo.pvptoggles.mixin.client;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.client.config.ClientConfig;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.components.Button;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PauseScreen.class)
public class PauseScreenMixin {
    @Shadow private @Nullable Button disconnectButton;

    @Inject(method = "tick", at = @At("TAIL"))
    private void render(CallbackInfo ci) {
        boolean render = !PVPTogglesClient.isInCombat;
        if (!ClientConfig.HANDLER.instance().isDisableExitButton()) render = true;
        if (disconnectButton != null) disconnectButton.active = render;
    }
}
