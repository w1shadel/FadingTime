package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.PlayerTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
public class TimeRenderHandler {
    public static double clientCost, clientMax;
    public static int clientTier = 0;
    private static float stopTransitionProgress = 0.0F;
    private static float realPartialTicks = 0.0F;

    public static void updateClientEffects() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) return;
        PlayerTimeMode pMode = TimeManager.getModeFor(mc.level);
        BossTimeMode bMode = BossTimeManager.getModeFor(mc.level);
        float modeValue = 0.0f;
        boolean active = false;
        float currentAccel = 1.0f;
        if (bMode == BossTimeMode.ABSOLUTE_STOP) {
            modeValue = 4.0f;
            active = true;
        } else if (pMode == PlayerTimeMode.REWINDING) {
            modeValue = 3.0f;
            active = true;
        } else if (pMode == PlayerTimeMode.STOPPED || bMode == BossTimeMode.STOPPED) {
            modeValue = 2.0f;
            active = true;
        } else if (pMode == PlayerTimeMode.ACCELERATING || bMode == BossTimeMode.ACCELERATING) {
            modeValue = 1.0f;
            active = true;
            currentAccel = (pMode == PlayerTimeMode.ACCELERATING) ?
                    TimeManager.getPlayerAccelerationFactor(mc.player) :
                    BossTimeManager.getAccelFactorFor(mc.level);
        }
        if (active) {
            if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.loadEffect(new ResourceLocation("tutm", "shaders/post/time_spatial.json"));
            }
            if (stopTransitionProgress < 1.0f) {
                stopTransitionProgress += 0.05f;
            }
            if (stopTransitionProgress > 1.0f) stopTransitionProgress = 1.0f;
            updateShaderUniforms(modeValue, currentAccel);

        } else {
            if (mc.gameRenderer.currentEffect() != null && mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.shutdownEffect();
            }
            stopTransitionProgress = 0.0f;
        }
    }

    public static void setClientData(double c, double m, int t) {
        clientCost = c;
        clientMax = m;
        clientTier = t;
    }

    private static void updateShaderUniforms(float mode, float accelFactor) {
        var postChain = Minecraft.getInstance().gameRenderer.currentEffect();
        if (postChain == null) return;
        ((PostChainAccessor) postChain).getPasses().forEach(pass -> {
            var shader = pass.getEffect();
            shader.safeGetUniform("StopProgress").set(stopTransitionProgress);
            shader.safeGetUniform("Mode").set(mode);
            shader.safeGetUniform("Time").set((System.currentTimeMillis() % 100000) / 1000.0F);
            var accelUniform = shader.safeGetUniform("AccelFactor");
            if (accelUniform != null) {
                accelUniform.set(accelFactor);
            }
        });
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