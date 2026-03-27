package com.maxwell.tutm.mixin;

import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererTimeStopMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float tutm$adjustPartialTicks(float partialTicks, LivingEntity pEntity) {
        if (TimeManager.isTimeStopped()) {
            return TimeManager.isImmune(pEntity) ? TimeRenderHandler.getPartialTicks() : 0.0F;
        }
        if (TimeManager.getAccelerationFactor(pEntity) > 1 || TimeManager.isRewinding()) {
            return 1.0F;
        }
        return partialTicks;
    }
}