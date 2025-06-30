package me.playgamesgo.pvptoggles.mixinaccess;

public interface IPVPEntity {
    boolean PVPToggles$isPVPEnabled();
    void PVPToggles$setPVPEnabled(boolean enabled);

    boolean PVPToggles$isDisablePVPAfterDelay();
    void PVPToggles$setDisablePVPAfterDelay();

    void PVPToggles$startCombat();
    boolean PVPToggles$isInCombat();
}
