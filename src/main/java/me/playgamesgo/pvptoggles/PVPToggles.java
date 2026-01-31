package me.playgamesgo.pvptoggles;

import dev.rollczi.litecommands.fabric.LiteFabricFactory;
import dev.rollczi.litecommands.fabric.server.FabricServerSender;
import dev.rollczi.litecommands.permission.PermissionDefaultResolver;
import me.lucko.fabric.api.permissions.v0.Permissions;
import me.playgamesgo.pvptoggles.commands.PVPAdminCommand;
import me.playgamesgo.pvptoggles.commands.PVPToggleCommand;
import me.playgamesgo.pvptoggles.utils.Config;
import me.playgamesgo.pvptoggles.utils.PacketRegistry;
import me.playgamesgo.pvptoggles.utils.PlaceholdersUtil;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.text.Component;
import net.minecraft.command.DefaultPermissions;
import net.minecraft.server.command.ServerCommandSource;

public final class PVPToggles implements DedicatedServerModInitializer {
    public static final String MOD_ID = "pvptoggles";
    public static final boolean isEclipseCustomNameLoaded = FabricLoader.getInstance().isModLoaded("eclipsescustomname");

    @Override
    public void onInitializeServer() {
        Config.HANDLER.load();
        PlaceholdersUtil.init();
        PacketRegistry.registerPackets();
        PacketRegistry.registerC2SPacketListeners();

        LiteFabricFactory.server()
                .permissionResolver(new PermissionDefaultResolver((sender, permission) -> {
                    if (sender instanceof FabricServerSender serverSender) {
                        ServerCommandSource player = (ServerCommandSource) serverSender.getHandle();
                        if (player.getPermissions().hasPermission(DefaultPermissions.OWNERS)) return true;

                        return Permissions.check(player, permission);
                    }
                    return false;
                }))
                .commands(
                        new PVPToggleCommand(),
                        new PVPAdminCommand()
                )
                .result(Component.class, (invocation, result, chain) -> invocation.sender().sendMessage(result))
                .build();
    }
}
