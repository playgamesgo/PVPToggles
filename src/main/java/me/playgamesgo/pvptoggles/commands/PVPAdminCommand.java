package me.playgamesgo.pvptoggles.commands;

import dev.rollczi.litecommands.annotations.argument.Arg;
import dev.rollczi.litecommands.annotations.command.Command;
import dev.rollczi.litecommands.annotations.context.Context;
import dev.rollczi.litecommands.annotations.execute.Execute;
import dev.rollczi.litecommands.annotations.permission.Permission;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

@Command(name = "pvptoggleadmin")
@Permission("pvptoggles.command.pvptoggleadmin")
public final class PVPAdminCommand {
    @Execute(name = "reload")
    public void reload(@Context ServerCommandSource source) {
        Config.HANDLER.load();

        source.sendMessage(MiniMessage.miniMessage().deserialize(Config.HANDLER.instance().getConfigReloadedMessage()));
    }

    @Execute(name = "set")
    public void set(@Context ServerCommandSource source, @Arg PlayerEntity player, @Arg boolean enable) {
        IPVPEntity pvp = (IPVPEntity) player;
        pvp.PVPToggles$setPVPEnabled(enable, false);

        Config config = Config.HANDLER.instance();
        String message = enable ? config.getAdminSetPVPEnabledMessage() : config.getAdminSetPVPDisabledMessage();
        source.sendMessage(MiniMessage.miniMessage().deserialize(message.replace("{player}", player.getName().getString())));
    }
}
