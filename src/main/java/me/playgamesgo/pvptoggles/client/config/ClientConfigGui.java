package me.playgamesgo.pvptoggles.client.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.network.chat.Component;

public final class ClientConfigGui implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Component.literal("PVPToggles Client Config"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("General"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("General"))
                                .option(disableExitButton)
                                .option(displayPVPIcon)
                                .option(replaceCombatBar)
                                .build())
                        .build())
                .build()
                .generateScreen(parentScreen);
    }

    private final Option<Boolean> disableExitButton = Option.<Boolean>createBuilder()
            .name(Component.literal("Disable Exit Button"))
            .description(OptionDescription.of(Component.literal("Disables exit button while you are in combat.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isDisableExitButton(), newVal -> {
                ClientConfig.HANDLER.instance().setDisableExitButton(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();

    private final Option<Boolean> displayPVPIcon = Option.<Boolean>createBuilder()
            .name(Component.literal("Display PVP Icon"))
            .description(OptionDescription.of(Component.literal("Displays a PVP icon when PVP is enabled.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isDisplayPVPIcon(), newVal -> {
                ClientConfig.HANDLER.instance().setDisplayPVPIcon(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();

    private final Option<Boolean> replaceCombatBar = Option.<Boolean>createBuilder()
            .name(Component.literal("Replace Combat Bar"))
            .description(OptionDescription.of(Component.literal("Replaces the combat bar with a custom one.")))
            .binding(true, () -> ClientConfig.HANDLER.instance().isReplaceCombatBar(), newVal -> {
                ClientConfig.HANDLER.instance().setReplaceCombatBar(newVal);
                ClientConfig.HANDLER.save();
            })
            .controller(TickBoxControllerBuilder::create)
            .build();
}
