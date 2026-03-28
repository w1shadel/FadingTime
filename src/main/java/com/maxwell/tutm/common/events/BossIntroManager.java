package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class BossIntroManager {
    private static boolean bossSpawned = false;

    @SubscribeEvent
    public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        if (event.getTo() == TUTMDimensions.TIME_REALM_LEVEL_KEY) {
            ServerLevel level = (ServerLevel) event.getEntity().level();
            AABB checkArea = new AABB(-256, level.getMinBuildHeight(), -256, 256, level.getMaxBuildHeight(), 256);
            List<The_Ultimate_TimeManagerEntity> bosses = level.getEntitiesOfClass(The_Ultimate_TimeManagerEntity.class, checkArea);
            bossSpawned = !bosses.isEmpty();
        }
    }

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
        BossTimeManager.requestBossMode(level, BossTimeMode.ABSOLUTE_STOP, 120, 1);
        Component bossName = Component.translatable("entity.tutm.the_ultimate_time_manager").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        Component introMessage = Component.translatable("chat.tutm.message.one").withStyle(ChatFormatting.WHITE);
        Component finalMessage = Component.empty().append(bossName).append(introMessage);
        for (ServerPlayer sp : level.players()) {
            sp.sendSystemMessage(finalMessage);
        }
        The_Ultimate_TimeManagerEntity boss = new The_Ultimate_TimeManagerEntity(ModEntities.get(The_Ultimate_TimeManagerEntity.class), level);
        boss.moveTo(0.5, 70.0, 0.5, 180, 0);
        level.addFreshEntity(boss);
    }
}