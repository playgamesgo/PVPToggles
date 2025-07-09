package me.playgamesgo.pvptoggles.client.utils;

import me.playgamesgo.pvptoggles.client.config.ClientConfig;

import java.util.List;

public final class ClientCapabilities {
    public enum Capabilities {
        HEALTH_TRACKER
    }

    private static final ClientConfig config = ClientConfig.HANDLER.instance();

    public static boolean healthTracker = true;

    public static boolean isHealthTrackerAvailable() {
        return healthTracker && config.isEnableHealthTracker();
    }

    public static void setCapability(List<Capabilities> capabilities) {
        healthTracker = capabilities.contains(Capabilities.HEALTH_TRACKER);
    }
}
