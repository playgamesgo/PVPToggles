package me.playgamesgo.pvptoggles.client.utils;

import me.playgamesgo.pvptoggles.packets.TogglePVP;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class Keybinds {
    public static final KeybindWrapper togglePVP = (KeybindWrapper) KeyBindingHelper.registerKeyBinding(new KeybindWrapper(
            "key.pvptoggles.toggle_pvp",
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H,
            "key.categories.pvptoggles"
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (togglePVP.isReleased()) {
                ClientPlayNetworking.send(new TogglePVP(false));
            }
        });
    }
}
