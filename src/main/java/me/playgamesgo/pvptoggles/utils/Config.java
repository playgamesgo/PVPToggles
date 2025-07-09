package me.playgamesgo.pvptoggles.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.pvptoggles.PVPToggles;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.bossbar.BossBar;
import net.minecraft.util.Identifier;

@Getter @Setter
public final class Config {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of(PVPToggles.MOD_ID, "pvptoggles-server"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("pvptoggles-server.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    @SerialEntry
    private boolean saveInPlayerData = false;

    @SerialEntry
    private boolean defaultPVPEnabled = false;

    @SerialEntry
    private boolean addCompatFabricCustomNames = true;

    @SerialEntry
    private int pvpDisableDelaySeconds = 5;


    @SerialEntry
    private boolean enableCombatManager = true;

    @SerialEntry
    private int combatManagerPVPDurationSeconds = 30;

    @SerialEntry
    private boolean combatManagerShowBossBar = true;

    @SerialEntry
    private boolean combatManagerKillOnLogout = true;


    @SerialEntry
    private String pvpDisabledSelfMessage = "<red><bold>You cannot attack players while your PVP is disabled!</bold></red>";

    @SerialEntry
    private String pvpDisabledOtherMessage = "<red><bold>This player has PVP disabled! You cannot attack them!</bold></red>";


    @SerialEntry
    private String pvpEnabledMessage = "<green><bold>You have enabled PVP!</bold></green>";

    @SerialEntry
    private String pvpDisabledMessage = "<red><bold>You have disabled PVP!</bold></red>";


    @SerialEntry
    private String pvpDisableDelayMessage = "<yellow><bold>You are disabling PVP, it will be disabled in {timer} seconds!</bold></yellow>";

    @SerialEntry
    private String pvpDisableCancelledMessage = "<yellow><bold>Your disabling of PVP has been cancelled!</bold></yellow>";

    @SerialEntry
    private String pvpAlreadyDisablingMessage = "<yellow><bold>You are already disabling PVP!</bold></yellow>";

    @SerialEntry
    private String cantTogglePVPInCombatMessage = "<red><bold>You cannot toggle PVP while in combat!</bold></red>";

    @SerialEntry
    private String combatBossBarTitle = "<yellow>In combat time left: {timer} seconds</yellow>";

    @SerialEntry
    private BossBar.Color combatBossBarColor = BossBar.Color.YELLOW;


    @SerialEntry
    private String pvpEnabledPlaceholder = "☠️";

    @SerialEntry
    private String pvpDisabledPlaceholder = "";


    @SerialEntry
    private boolean allowClientHealthTracker = true;
}
