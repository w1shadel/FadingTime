package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.ClientTimeData;
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
    private static int clientAccelFactor = 1;
    private static float stopTransitionProgress = 0.0F;
    private static double clientCost, clientMax;
    private static int clientTier = 0;
    private static float realPartialTicks = 0.0F;

    public static void updateClientEffects() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) return;

        // ボスまたはプレイヤーの状態を統合して判定
        boolean stopped = (ClientTimeData.pMode == PlayerTimeMode.STOPPED) ||
                (ClientTimeData.bMode == BossTimeMode.STOPPED || ClientTimeData.bMode == BossTimeMode.ABSOLUTE_STOP);
        boolean absolute = (ClientTimeData.bMode == BossTimeMode.ABSOLUTE_STOP);
        boolean rewinding = (ClientTimeData.pMode == PlayerTimeMode.REWINDING);
        boolean accel = (clientAccelFactor > 1) || (ClientTimeData.bossAccel > 1);

        boolean active = stopped || rewinding || accel;

        if (active) {
            if (mc.gameRenderer.currentEffect() == null || !mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.loadEffect(new ResourceLocation("tutm", "shaders/post/time_spatial.json"));
            }

            float modeValue = 1.0f;
            if (absolute) modeValue = 4.0f;
            else if (rewinding) modeValue = 3.0f;
            else if (stopped) modeValue = 2.0f;
            else if (accel) modeValue = 1.5f;

            updateShaderUniforms(modeValue);

            if (stopped && stopTransitionProgress < 1.0f) {
                stopTransitionProgress += 0.02f;
            }
        } else {
            // エフェクト終了処理
            if (mc.gameRenderer.currentEffect() != null && mc.gameRenderer.currentEffect().getName().contains("time_spatial")) {
                mc.gameRenderer.shutdownEffect();
                stopTransitionProgress = 0.0f;
            }
        }
    }
    public static void setClientData(double c, double m, int t, int bossAccel) {
        clientCost = c;
        clientMax = m;
        clientTier = t;
        clientAccelFactor = bossAccel; // 必要に応じてプレイヤー側の加速倍率と統合してください
    }
    private static void updateShaderUniforms(float mode) {
        var postChain = Minecraft.getInstance().gameRenderer.currentEffect();
        if (postChain == null) return;
        ((PostChainAccessor) postChain).getPasses().forEach(pass -> {
            var shader = pass.getEffect();
            shader.safeGetUniform("StopProgress").set(stopTransitionProgress);
            shader.safeGetUniform("Mode").set(mode);
            shader.safeGetUniform("Time").set((System.currentTimeMillis() % 100000) / 1000.0F);
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