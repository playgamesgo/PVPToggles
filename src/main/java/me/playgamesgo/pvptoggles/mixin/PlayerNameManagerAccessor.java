package me.playgamesgo.pvptoggles.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xyz.eclipseisoffline.eclipsescustomname.PlayerNameManager;

@Mixin(value = PlayerNameManager.class, remap = false)
public interface PlayerNameManagerAccessor {
    @Invoker(value = "markDirty", remap = false)
    void PVPToggles$markDirty(ServerPlayerEntity player);
}
