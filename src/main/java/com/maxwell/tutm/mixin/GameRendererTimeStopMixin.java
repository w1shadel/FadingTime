package com.maxwell.tutm.mixin;

import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererTimeStopMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void tutm$captureTicks(float pPartialTicks, long pFinishTimeNano, PoseStack pPoseStack, CallbackInfo ci) {
        TimeRenderHandler.setPartialTicks(pPartialTicks);
    }

}