package me.playgamesgo.pvptoggles.utils;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.pvptoggles.PVPToggles;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

@Getter @Setter
public final class Config {
    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of(PVPToggles.MOD_ID, "pvptoggles"))
                    .serializer(config -> GsonConfigSerializerBuilder.create(config)
                            .setPath(FabricLoader.getInstance().getConfigDir().resolve("pvptoggles.json5"))
                            .setJson5(true)
                            .build())
                    .build();

    @SerialEntry
    private boolean saveInPlayerData = false;

    @SerialEntry
    private boolean defaultPVPEnabled = false;


    @SerialEntry
    private String pvpDisabledSelfMessage = "<red><bold>You cannot attack players while your PVP is disabled!</bold></red>";

    @SerialEntry
    private String pvpDisabledOtherMessage = "<red><bold>This player has PVP disabled! You cannot attack them!</bold></red>";


    @SerialEntry
    private String pvpEnabledMessage = "<green><bold>You have enabled PVP!</bold></green>";

    @SerialEntry
    private String pvpDisabledMessage = "<red><bold>You have disabled PVP!</bold></red>";


    @SerialEntry
    private boolean displayPVPStatusInPlayerName = true;

    @SerialEntry
    private String pvpEnabledPlaceholder = "☠️";

    @SerialEntry
    private String pvpDisabledPlaceholder = "";
}
