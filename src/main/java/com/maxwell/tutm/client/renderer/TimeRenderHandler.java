package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * クライアント側でのみロードされる描画管理クラス
 */
@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class TimeRenderHandler {
    private static float stopTransitionProgress = 0.0F;
    private static double clientCost, clientMax;
    private static int clientTier = 0;
    private static float realPartialTicks = 0.0F;

    public static void updateClientEffects() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.gameRenderer == null) return;
        boolean isAccelerating = TimeManager.getAccelerationFactor(mc.player) > 1;
        boolean active = TimeManager.isTimeStopped() || TimeManager.isRewinding() || isAccelerating;
        if (active) {
            if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.loadEffect(new ResourceLocation("tutm", "shaders/post/time_spatial.json"));
            }
            updateShaderUniforms();
            if (TimeManager.isTimeStopped() && stopTransitionProgress < 1.0F) stopTransitionProgress += 0.02F;
        } else {
            if (mc.gameRenderer.currentEffect() != null && mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.shutdownEffect();
                stopTransitionProgress = 0.0F;
            }
        }
    }

    private static void updateShaderUniforms() {
        var postChain = Minecraft.getInstance().gameRenderer.currentEffect();
        if (postChain == null) return;
        float mode = TimeManager.isRewinding() ? 3.0f : (TimeManager.isTimeStopped() ? 2.0f : 1.0f);
        ((PostChainAccessor) postChain).getPasses().forEach(pass -> {
            var shader = pass.getEffect();
            if (shader.getName().contains("time_spatial")) {
                shader.safeGetUniform("StopProgress").set(stopTransitionProgress);
                shader.safeGetUniform("Time").set((System.currentTimeMillis() % 100000) / 1000.0F);
                shader.safeGetUniform("Mode").set(mode);
            }
        });
    }

    public static void setClientData(double c, double m, int t) {
        clientCost = c;
        clientMax = m;
        clientTier = t;
    }

    public static double getCostRatio() {
        return clientMax > 0 ? clientCost / clientMax : 0;
    }

    public static int getTier() {
        return clientTier;
    }

    public static float getPartialTicks() {
        return realPartialTicks;
    }

    public static void setPartialTicks(float pt) {
        realPartialTicks = pt;
    }
}