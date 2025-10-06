package me.playgamesgo.pvptoggles.mixinaccess;

public interface IPVPEntity {
    boolean PVPToggles$isPVPEnabled();
    void PVPToggles$setPVPEnabled(boolean enabled);

    boolean PVPToggles$isDisablePVPAfterDelay();
    void PVPToggles$setDisablePVPAfterDelay();
    void PVPToggles$setDisablePVPAfterDelay(boolean enabled);

    void PVPToggles$setPVPDelayTimer(int ticks);
    int PVPToggles$getPvpDelayTimer();

    void PVPToggles$startCombat();
    boolean PVPToggles$isInCombat();
    void PVPToggles$resetCombatTimer();

    boolean PVPToggles$hasClientMod();
    void PVPToggles$setHasClientMod(boolean hasClientMod);
}
