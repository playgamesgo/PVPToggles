package me.playgamesgo.pvptoggles.utils;

import me.playgamesgo.pvptoggles.PVPToggles;
import net.minecraft.util.Identifier;

public final class PVPTogglesConstants {
    public static final Identifier HANDSHAKE_PACKET_ID = Identifier.of(PVPToggles.MOD_ID, "handshake");
    public static final Identifier COMBAT_PACKET_ID = Identifier.of(PVPToggles.MOD_ID, "combat");
    public static final Identifier TOGGLE_PVP_PACKET_ID = Identifier.of(PVPToggles.MOD_ID, "toggle_pvp");
}