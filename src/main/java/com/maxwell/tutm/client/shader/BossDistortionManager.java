package com.maxwell.tutm.client.shader;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, value = Dist.CLIENT)
public class BossDistortionManager {
    private static final ResourceLocation SHADER_LOCATION = TUTM.getResourceLocation("shaders/post/time_distortion.json");
    private static final float MAX_DISTANCE = 48.0F;
    private static final float PEAK_DISTANCE = 4.0F;
    private static float currentIntensity = 0.0F;
    private static boolean isOurShaderLoaded = false;
    private static int scanCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) return;
        float targetIntensity = 0.0F;
        if (scanCooldown-- <= 0) {
            scanCooldown = 5;
            The_Ultimate_TimeManagerEntity nearestBoss = null;
            double minSizeSq = MAX_DISTANCE * MAX_DISTANCE;
            for (var entity : mc.level.entitiesForRendering()) {
                if (entity instanceof The_Ultimate_TimeManagerEntity boss) {
                    double distSq = boss.distanceToSqr(mc.player);
                    if (distSq < minSizeSq) {
                        minSizeSq = distSq;
                        nearestBoss = boss;
                    }
                }
            }
            if (nearestBoss != null) {
                float dist = (float) Math.sqrt(minSizeSq);
                targetIntensity = 1.0F - (Math.max(0, dist - PEAK_DISTANCE) / (MAX_DISTANCE - PEAK_DISTANCE));
                targetIntensity = Math.max(0, Math.min(1.0F, targetIntensity));
            }
        } else {
            targetIntensity = currentIntensity;
        }
        currentIntensity = currentIntensity + (targetIntensity - currentIntensity) * 0.1F;
        boolean isOtherShaderWorking = TimeManager.isTimeStopped() || TimeManager.isRewinding() || TimeManager.getAccelerationFactor(mc.player) > 1;
        boolean shouldEffectBeVisible = currentIntensity > 0.01F && !isOtherShaderWorking;
        if (shouldEffectBeVisible) {
            if (mc.gameRenderer.currentEffect() == null || !isOurShaderLoaded) {
                mc.gameRenderer.loadEffect(SHADER_LOCATION);
                isOurShaderLoaded = true;
            }
            updateShaderUniforms(mc, currentIntensity);
        } else {
            if (isOurShaderLoaded) {
                updateShaderUniforms(mc, 0.0F);
                if (currentIntensity <= 0.001F) {
                    isOurShaderLoaded = false;
                }
            }
        }
    }

    private static void updateShaderUniforms(Minecraft mc, float intensity) {
        var postChain = mc.gameRenderer.currentEffect();
        if (postChain == null) return;
        try {
            var passes = ((PostChainAccessor) postChain).getPasses();
            for (var pass : passes) {
                var shader = pass.getEffect();
                if (isOurShaderLoaded && shader.getName().contains("time_distortion")) {
                    var uniform = shader.safeGetUniform("Intensity");
                    if (uniform != null) uniform.set(intensity);
                }
            }
        } catch (Exception ignored) {
        }
    }
}