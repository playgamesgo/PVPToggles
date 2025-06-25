package me.playgamesgo.pvptoggles.mixinaccess;

import net.minecraft.text.Text;

public interface IPVPEntity {
    boolean PVPToggles$isPVPEnabled();
    void PVPToggles$setPVPEnabled(boolean enabled);

    Text PVPToggles$getFormatedDisplayName();
}
