package me.playgamesgo.pvptoggles.client.utils;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;

public final class KeybindWrapper extends KeyMapping {
    private boolean wasPressed;

    public KeybindWrapper(String translationKey, int code, Category category) {
        super(translationKey, code, category);
        wasPressed = false;
    }

    public KeybindWrapper(String translationKey, InputConstants.Type type, int code, Category category) {
        super(translationKey, type, code, category);
        this.wasPressed = false;
    }

    public boolean isReleased() {
        if (!this.isDown() && wasPressed) {
            wasPressed = false;
            return true;
        }
        if (this.isDown()) {
            wasPressed = true;
        }
        return false;
    }
}
