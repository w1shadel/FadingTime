package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.world.TUTMDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tutm", value = Dist.CLIENT)
public class TemporalAnomalyClientEffect {
    private static final Vec3 MONOLITH_POS = new Vec3(50, 0, 50);
    private static final double SPAWN_RADIUS = 8.0;
    private static final int PARTICLE_COUNT = 3;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level != null && level.dimension() == TUTMDimensions.TIME_REALM_LEVEL_KEY) {
            if (mc.player.distanceToSqr(MONOLITH_POS.x, MONOLITH_POS.y, MONOLITH_POS.z) < 100 * 100) {
                spawnReversedParticles(level);
            }
        }
    }

    private static void spawnReversedParticles(ClientLevel level) {
        RandomSource random = level.random;
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double px = MONOLITH_POS.x + (random.nextDouble() - 0.5) * SPAWN_RADIUS * 2;
            double py = MONOLITH_POS.y - 5;
            double pz = MONOLITH_POS.z + (random.nextDouble() - 0.5) * SPAWN_RADIUS * 2;
            double vy = 0.1 + random.nextDouble() * 0.2;
            double vx = 0.02;
            double vz = 0.02;
            level.addParticle(ParticleTypes.END_ROD, px, py, pz, vx, vy, vz);
            if (random.nextFloat() > 0.7f) {
                level.addParticle(ParticleTypes.PORTAL, px, py + 2, pz, vx, vy * 0.5, vz);
            }
            if (random.nextFloat() > 0.5f) {
                level.addParticle(ParticleTypes.GLOW, px, py, pz, vx, vy, vz);
            }
        }
    }
}