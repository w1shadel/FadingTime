package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LevelRenderer.class)
public class LevelRendererTimeStopMixin {
    @ModifyVariable(method = "renderSky", at = @At("HEAD"), argsOnly = true)
    private float tutm$freezeSky(float partialTicks) {
        return TimeManager.isTimeStopped() ? 0.0F : partialTicks;
    }

    @ModifyVariable(method = "renderClouds", at = @At("HEAD"), argsOnly = true)
    private float tutm$freezeClouds(float partialTicks) {
        return TimeManager.isTimeStopped() ? 0.0F : partialTicks;
    }
}