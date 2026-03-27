package com.maxwell.tutm.mixin;

import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherTimeStopMixin {
    @ModifyVariable(method = "render", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float tutm$smartPartialTicks(float pPartialTicks, Entity pEntity) {
        if (TimeManager.isTimeStopped()) {
            return TimeManager.isImmune(pEntity) ? TimeRenderHandler.getPartialTicks() : 0.0F;
        }
        return pPartialTicks;
    }
}