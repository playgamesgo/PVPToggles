package me.playgamesgo.pvptoggles.client.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.pvptoggles.PVPToggles;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

@Getter
@Setter
public final class ClientConfig {
    public static ConfigClassHandler<ClientConfig> HANDLER = ConfigClassHandler.createBuilder(ClientConfig.class)
            .id(Identifier.of(PVPToggles.MOD_ID, "pvptoggles-client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("pvptoggles-client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    private boolean disableExitButton = true;

    @SerialEntry
    private boolean displayPVPIcon = true;

    @SerialEntry
    private boolean replaceCombatBar = true;
}
