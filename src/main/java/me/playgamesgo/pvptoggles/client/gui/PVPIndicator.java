package me.playgamesgo.pvptoggles.client.gui;

import me.playgamesgo.pvptoggles.client.PVPTogglesClient;
import me.playgamesgo.pvptoggles.client.config.ClientConfig;
import me.playgamesgo.pvptoggles.utils.PVPTogglesConstants;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;

public final class PVPIndicator {
    public static void init() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, PVPTogglesConstants.PVP_INDICATOR_ID, PVPIndicator::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ClientConfig.HANDLER.instance().isDisplayPVPIcon()) return;
        if (!PVPTogglesClient.isPVPEnabled) return;
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, InGameHud.HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 0, context.getScaledWindowHeight() - 18, 18, 18);
    }
}
