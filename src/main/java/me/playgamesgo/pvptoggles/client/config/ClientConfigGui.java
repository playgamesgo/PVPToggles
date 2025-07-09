package me.playgamesgo.pvptoggles.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.text.Text;

public final class ClientConfigGui implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("PVPToggles Client Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("General"))
                                .option(disableExitButton)
                                .option(displayPVPIcon)
                                .option(replaceCombatBar)
                                .build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }

    private final Option<Boolean> disableExitButton = Option.<Boolean>createBuilder()
            .name(Text.literal("Disable Exit Button"))
            .description(OptionDescription.of(Text.literal("Disables exit button while you are in combat.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isDisableExitButton(), newVal -> {
                ClientConfig.HANDLER.instance().setDisableExitButton(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();

    private final Option<Boolean> displayPVPIcon = Option.<Boolean>createBuilder()
            .name(Text.literal("Display PVP Icon"))
            .description(OptionDescription.of(Text.literal("Displays a PVP icon when PVP is enabled.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isDisplayPVPIcon(), newVal -> {
                ClientConfig.HANDLER.instance().setDisplayPVPIcon(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();

    private final Option<Boolean> replaceCombatBar = Option.<Boolean>createBuilder()
            .name(Text.literal("Replace Combat Bar"))
            .description(OptionDescription.of(Text.literal("Replaces the combat bar with a custom one.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isReplaceCombatBar(), newVal -> {
                ClientConfig.HANDLER.instance().setReplaceCombatBar(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();
}
