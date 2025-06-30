package me.playgamesgo.pvptoggles.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.playgamesgo.pvptoggles.PVPToggles;
import me.playgamesgo.pvptoggles.mixin.PlayerNameManagerAccessor;
import me.playgamesgo.pvptoggles.mixinaccess.IPVPEntity;
import me.playgamesgo.pvptoggles.utils.Config;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import xyz.eclipseisoffline.eclipsescustomname.CustomName;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;

public final class PVPToggleCommand {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("pvp")
                    .requires(ServerCommandSource::isExecutedByPlayer)
                    .executes(PVPToggleCommand::execute)
                    .then(CommandManager.argument("enable", BoolArgumentType.bool())
                            .executes(PVPToggleCommand::executeWithArg)));

            dispatcher.register(CommandManager.literal("pvptoggle")
                    .requires(ServerCommandSource::isExecutedByPlayer)
                    .executes(PVPToggleCommand::execute)
                    .then(CommandManager.argument("enable", BoolArgumentType.bool())
                            .executes(PVPToggleCommand::executeWithArg)));
        });
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        PlayerEntity player = context.getSource().getPlayer();
        IPVPEntity pvp = (IPVPEntity) player;
        return run(context, !pvp.PVPToggles$isPVPEnabled());
    }

    private static int executeWithArg(CommandContext<ServerCommandSource> context) {
        boolean enable = BoolArgumentType.getBool(context, "enable");
        return run(context, enable);
    }

    private static int run(CommandContext<ServerCommandSource> context, boolean enable) {
        Config config = Config.HANDLER.instance();
        ServerPlayerEntity player = context.getSource().getPlayer();
        IPVPEntity pvp = (IPVPEntity) player;

        if (pvp.PVPToggles$isInCombat()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getCantTogglePVPInCombatMessage()));
            return 1;
        }

        if (pvp.PVPToggles$isDisablePVPAfterDelay()) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(config.getPvpAlreadyDisablingMessage()));
            return 1;
        }

        if (enable) pvp.PVPToggles$setPVPEnabled(true);
        else pvp.PVPToggles$setDisablePVPAfterDelay();
        return 1;
    }
}
