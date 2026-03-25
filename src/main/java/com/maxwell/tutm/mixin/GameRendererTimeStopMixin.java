package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.TimeManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererTimeStopMixin {
    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void tutm$captureTicks(float pPartialTicks, long pFinishTimeNano, PoseStack pPoseStack, CallbackInfo ci) {
        TimeManager.setRealPartialTicks(pPartialTicks);
    }

    @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;)V"))
    private void tutm$redirectRenderLevel(LevelRenderer instance, PoseStack pPoseStack, float pPartialTicks, long pFinishTimeNano, boolean pRenderBlockOutline, net.minecraft.client.Camera pCamera, GameRenderer pGameRenderer, net.minecraft.client.renderer.LightTexture pLightTexture, Matrix4f pProjectionMatrix) {
        float f = TimeManager.isTimeStopped() ? 0.0F : pPartialTicks;
        instance.renderLevel(pPoseStack, f, pFinishTimeNano, pRenderBlockOutline, pCamera, pGameRenderer, pLightTexture, pProjectionMatrix);
    }

}