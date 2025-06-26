package me.playgamesgo.pvptoggles.mixinaccess;

public interface IPVPEntity {
    boolean PVPToggles$isPVPEnabled();
    void PVPToggles$setPVPEnabled(boolean enabled);
}
