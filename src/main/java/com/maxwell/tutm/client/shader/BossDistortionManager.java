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

@Mod.EventBusSubscriber(modid = "tutm", value = Dist.CLIENT)
public class BossDistortionManager {
    private static final ResourceLocation SHADER_LOCATION = TUTM.getResourceLocation("shaders/post/time_distortion.json");
    private static final float MAX_DISTANCE = 48.0F; // Effect starts at this distance
    private static final float PEAK_DISTANCE = 4.0F; // Maximum intensity at this distance
    
    private static float currentIntensity = 0.0F;
    private static boolean isShaderActive = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) return;

        // 1. Find the nearest TimeManager boss
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

        // 2. Calculate target intensity
        float targetIntensity = 0.0F;
        if (nearestBoss != null) {
            float dist = (float) Math.sqrt(minSizeSq);
            // Linear interpolation: 0.0 at MAX_DISTANCE, 1.0 at PEAK_DISTANCE
            targetIntensity = 1.0F - (Math.max(0, dist - PEAK_DISTANCE) / (MAX_DISTANCE - PEAK_DISTANCE));
            targetIntensity = Math.max(0, Math.min(1.0F, targetIntensity));
        }

        // Smooth intensity transition
        currentIntensity = Math.max(0, Math.min(1.0F, currentIntensity + (targetIntensity - currentIntensity) * 0.1F));

        // 3. Manage Shader Loading
        // To avoid conflict with TimeManager, we only load our shader if TimeManager is NOT using its shader
        // and our intensity is significant.
        boolean shouldBeActive = currentIntensity > 0.01F && !TimeManager.isTimeStopped() && !TimeManager.isRewinding() && TimeManager.getAccelerationFactor(mc.player) <= 1;

        if (shouldBeActive) {
            if (mc.gameRenderer.currentEffect() == null) {
                mc.gameRenderer.loadEffect(SHADER_LOCATION);
                isShaderActive = true;
            }
            updateShaderUniforms(mc, currentIntensity);
        } else {
            if (isShaderActive) {
                var chain = mc.gameRenderer.currentEffect();
                if (chain != null) {
                    try {
                        var passes = ((PostChainAccessor) chain).getPasses();
                        boolean isMyShader = false;
                        for (var pass : passes) {
                            if (pass.getEffect().getName().contains("time_distortion")) {
                                isMyShader = true;
                                break;
                            }
                        }
                        if (isMyShader) {
                            mc.gameRenderer.shutdownEffect();
                        }
                    } catch (Exception e) {}
                }
                isShaderActive = false;
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
                if (shader.getName().contains("time_distortion")) {
                    shader.safeGetUniform("Intensity").set(intensity);
                }
            }
        } catch (Exception e) {
            // Silently fail if mixin fails or uniform is missing
        }
    }
}
