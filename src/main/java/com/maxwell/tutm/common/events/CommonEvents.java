package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.common.world.TimeRealmGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class CommonEvents {
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide()) return;
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (player.level().dimension() == Level.END) {
                if (TimeManager.getAccelerationFactor(player) >= 10.0F) {
                    event.setCanceled(true);
                    moveToTimeRealm(player);
                }
            }
        }
    }

    private static void moveToTimeRealm(ServerPlayer player) {
        ServerLevel destination = player.server.getLevel(TUTMDimensions.TIME_REALM_LEVEL_KEY);
        if (destination != null) {
            double destX = 0.5;
            double destY = 65.0;
            double destZ = -45.0;
            player.fallDistance = 0;
            player.setDeltaMovement(0, 0, 0);
            player.teleportTo(destination, destX, destY, destZ, 0, 0);
            player.setInvulnerable(true);
            TimeRealmGenerator.generateArena(destination, new BlockPos(0, 60, 0));
        }
    }
}