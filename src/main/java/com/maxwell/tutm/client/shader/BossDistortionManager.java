package com.maxwell.tutm.client.shader;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.PlayerTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.mixin.PostChainAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = TUTM.MODID, value = Dist.CLIENT)
public class BossDistortionManager {
    private static final ResourceLocation SHADER_LOCATION = new ResourceLocation(TUTM.MODID, "shaders/post/time_distortion.json");
    private static final float MAX_DISTANCE = 48.0F;
    private static final float PEAK_DISTANCE = 4.0F;
    private static float currentIntensity = 0.0F;
    private static float targetIntensity = 0.0F;
    private static boolean isOurShaderLoaded = false;
    private static int scanCooldown = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.gameRenderer == null) return;
        if (scanCooldown-- <= 0) {
            scanCooldown = 5;
            targetIntensity = 0.0F;
            AABB searchBox = mc.player.getBoundingBox().inflate(MAX_DISTANCE);
            List<The_Ultimate_TimeManagerEntity> bosses = mc.level.getEntitiesOfClass(The_Ultimate_TimeManagerEntity.class, searchBox);
            double minSizeSq = MAX_DISTANCE * MAX_DISTANCE;
            for (var boss : bosses) {
                double distSq = boss.distanceToSqr(mc.player);
                if (distSq < minSizeSq) {
                    minSizeSq = distSq;
                }
            }
            if (minSizeSq < MAX_DISTANCE * MAX_DISTANCE) {
                float dist = (float) Math.sqrt(minSizeSq);
                targetIntensity = 1.0F - (Math.max(0, dist - PEAK_DISTANCE) / (MAX_DISTANCE - PEAK_DISTANCE));
                targetIntensity = Math.max(0, Math.min(1.0F, targetIntensity));
            }
        }
        currentIntensity += (targetIntensity - currentIntensity) * 0.1F;
        if (currentIntensity < 0.001F) currentIntensity = 0.0F;
        PlayerTimeMode pMode = TimeManager.getModeFor(mc.level);
        BossTimeMode bMode = BossTimeManager.getModeFor(mc.level);
        boolean isOtherShaderWorking = (pMode != PlayerTimeMode.NORMAL) || (bMode != BossTimeMode.NORMAL);
        boolean shouldEffectBeVisible = currentIntensity > 0.01F && !isOtherShaderWorking;
        if (shouldEffectBeVisible) {
            if (mc.gameRenderer.currentEffect() == null || !isOurShaderLoaded) {
                mc.gameRenderer.loadEffect(SHADER_LOCATION);
                isOurShaderLoaded = true;
            }
            updateShaderUniforms(mc, currentIntensity);
        } else {
            if (isOurShaderLoaded) {
                if (mc.gameRenderer.currentEffect() != null && mc.gameRenderer.currentEffect().getName().contains("time_distortion")) {
                    mc.gameRenderer.shutdownEffect();
                }
                isOurShaderLoaded = false;
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
                    var uniform = shader.safeGetUniform("Intensity");
                    if (uniform != null) uniform.set(intensity);
                }
            }
        } catch (Exception ignored) {
        }
    }
}