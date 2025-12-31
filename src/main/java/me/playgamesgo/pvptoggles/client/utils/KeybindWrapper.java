package me.playgamesgo.pvptoggles.client.utils;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public final class KeybindWrapper extends KeyBinding {
    private boolean wasPressed;

    public KeybindWrapper(String translationKey, int code, Category category) {
        super(translationKey, code, category);
        wasPressed = false;
    }

    public KeybindWrapper(String translationKey, InputUtil.Type type, int code, Category category) {
        super(translationKey, type, code, category);
        this.wasPressed = false;
    }

    public boolean isReleased() {
        if (!this.isPressed() && wasPressed) {
            wasPressed = false;
            return true;
        }
        if (this.isPressed()) {
            wasPressed = true;
        }
        return false;
    }
}
