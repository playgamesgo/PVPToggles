package me.playgamesgo.pvptoggles.client.gui;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.client.config.ClientConfig;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.DeltaTracker;

public final class HudRenderer {
    public static void init() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, PVPTogglesConstants.PVP_INDICATOR_ID, HudRenderer::renderIndicator);
    }

    private static void renderIndicator(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        if (!ClientConfig.HANDLER.instance().isDisplayPVPIcon()) return;
        if (!PVPTogglesClient.isPVPEnabled) return;
        context.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 0, context.guiHeight() - 18, 18, 18);
    }
}
