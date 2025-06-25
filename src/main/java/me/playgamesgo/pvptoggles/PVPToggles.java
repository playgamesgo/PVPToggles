package me.playgamesgo.pvptoggles;

import lombok.Getter;
import me.playgamesgo.pvptoggles.commands.PVPToggleCommand;
import me.playgamesgo.pvptoggles.utils.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

public final class PVPToggles implements ModInitializer {
    public static final String MOD_ID = "pvptoggles";
    @Getter private static Team pvpEnabledTeam, pvpDisabledTeam;

    @Override
    public void onInitialize() {
        Config.HANDLER.load();
        PVPToggleCommand.init();

        if (Config.HANDLER.instance().isDisplayPVPStatusInPlayerName()) {
            ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                pvpEnabledTeam = server.getScoreboard().getTeam("pvptoggles.pvp_enabled");
                if (pvpEnabledTeam == null) {
                    pvpEnabledTeam = server.getScoreboard().addTeam("pvptoggles.pvp_enabled");
                    pvpEnabledTeam.setSuffix(Text.literal(Config.HANDLER.instance().getPvpEnabledPlaceholder()));
                }

                pvpDisabledTeam = server.getScoreboard().getTeam("pvptoggles.pvp_disabled");
                if (pvpDisabledTeam == null) {
                    pvpDisabledTeam = server.getScoreboard().addTeam("pvptoggles.pvp_disabled");
                    pvpDisabledTeam.setSuffix(Text.literal(Config.HANDLER.instance().getPvpDisabledPlaceholder()));
                }
            });
        }
    }
}
