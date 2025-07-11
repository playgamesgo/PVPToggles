package me.playgamesgo.pvptoggles.mixin.client;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    @Shadow private @Nullable ButtonWidget exitButton;

    @Inject(method = "render", at = @At("TAIL"))
    private void render(CallbackInfo ci) {
        if (exitButton != null) exitButton.active = !PVPTogglesClient.isInCombat;
    }
}
