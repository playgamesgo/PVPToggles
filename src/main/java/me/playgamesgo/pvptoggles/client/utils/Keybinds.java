package me.playgamesgo.pvptoggles.client.utils;

import me.playgamesgo.pvptoggles.packets.TogglePVP;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static final KeybindWrapper togglePVP = (KeybindWrapper) KeyMappingHelper.registerKeyMapping(new KeybindWrapper(
            "key.pvptoggles.toggle_pvp",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("pvptoggles", "pvptoggles"))
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (togglePVP.isReleased()) {
                ClientPlayNetworking.send(new TogglePVP(false));
            }
        });
    }
}
