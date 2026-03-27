package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.init.ModEntities;
import com.maxwell.tutm.init.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class BossIntroManager {
    private static boolean bossSpawned = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        ServerLevel level = player.serverLevel();
        if (level.dimension() == TUTMDimensions.TIME_REALM_LEVEL_KEY && !bossSpawned) {
            if (player.distanceToSqr(0, player.getY(), 0) < 10 * 10) {
                spawnBossWithCeremony(level, player);
                bossSpawned = true;
            }
        }
    }

    private static void spawnBossWithCeremony(ServerLevel level, ServerPlayer player) {
        TimeManager.forceNormalize();
        TimeManager.startBossTimeStop(level, 60);
        The_Ultimate_TimeManagerEntity boss = new The_Ultimate_TimeManagerEntity(ModEntities.get(The_Ultimate_TimeManagerEntity.class), level);
        level.addFreshEntity(boss);
        boss.moveTo(0.5, 70.0, 0.5, 180, 0);
        boss.setInvulnerable(true);
        boss.setNoAi(true);
        level.playSound(null, 0, 70, 0, ModSounds.TIME_STOP.get(), SoundSource.HOSTILE, 1.0f, 0.5f);
    }
}