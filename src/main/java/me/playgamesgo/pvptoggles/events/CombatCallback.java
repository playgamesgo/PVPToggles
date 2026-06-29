package me.playgamesgo.pvptoggles.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public interface CombatCallback {
    Event<CombatCallback> EVENT = EventFactory.createArrayBacked(CombatCallback.class,
            event -> (player, inCombat) -> {
                for (CombatCallback callback : event) {
                    callback.onCombatChange(player, inCombat);
                }
            }
    );

    void onCombatChange(Player player, boolean inCombat);
}
