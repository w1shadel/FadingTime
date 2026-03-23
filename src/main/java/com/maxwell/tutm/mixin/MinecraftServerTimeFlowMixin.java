package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerTimeFlowMixin {

    @Redirect(
            method = "tickChildren",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V")
    )
    private void tutm$redirectLevelTick(ServerLevel serverlevel, BooleanSupplier pHasTimeLeft) {
        if (TimeManager.isTimeStopped()) {
            TimeManager.tickImmuneEntitiesOnly(serverlevel);
            return;
        }
        if (TimeManager.isRewinding()) {
            TimeManager.handleRewindTick(serverlevel);
            return;
        }
        int factor = TimeManager.getAccelerationFactor(null);
        for (int i = 0; i < factor; i++) {
            serverlevel.tick(pHasTimeLeft);
        }
    }
}