package me.playgamesgo.pvptoggles.mixin;

import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.utils.Config;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void playerJoinEvent(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        @Nullable Team team = player.getScoreboardTeam();
        if (team == null || !team.getName().equals("pvptoggles.pvp_enabled") && !team.getName().equals("pvptoggles.pvp_disabled")) {
            player.getScoreboard().addScoreHolderToTeam(player.getNameForScoreboard(),
                    Config.HANDLER.instance().isDefaultPVPEnabled() ? PVPToggles.getPvpEnabledTeam() : PVPToggles.getPvpDisabledTeam());
        }
    }
}
