package me.playgamesgo.pvptoggles.commands;

import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.optional.OptionalArg;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.server.network.ServerPlayerEntity;

@Command(name = "pvp", aliases = "pvptoggle")
public final class PVPToggleCommand {
    @Execute
    public void execute(@Context ServerPlayerEntity context, @OptionalArg Boolean enable) {
        if (enable == null) {
            IPVPEntity pvp = (IPVPEntity) context;
            enable = !pvp.PVPToggles$isPVPEnabled();
        }

        setPVP(context, enable);
    }

    private void setPVP(ServerPlayerEntity player, boolean enable) {
        Config config = Config.HANDLER.instance();
        IPVPEntity pvp = (IPVPEntity) player;

        if (pvp.PVPToggles$isInCombat()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getCantTogglePVPInCombatMessage()));
            return;
        }

        if (pvp.PVPToggles$isDisablePVPAfterDelay()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpAlreadyDisablingMessage()));
            return;
        }

        if (enable) pvp.PVPToggles$setPVPEnabled(true);
        else pvp.PVPToggles$setDisablePVPAfterDelay();
    }
}
