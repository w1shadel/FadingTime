package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class FinalSacrificeEvent {

    @SubscribeEvent
    public static void onPlayerDeathInVoid(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            ItemStack clock = player.getMainHandItem().is(ModItems.ABSOLUTE_CHRONOS_CLOCK.get()) ? player.getMainHandItem() :
                    (player.getOffhandItem().is(ModItems.ABSOLUTE_CHRONOS_CLOCK.get()) ? player.getOffhandItem() : ItemStack.EMPTY);

            if (!clock.isEmpty()) {
                event.setCanceled(true);
                player.setHealth(player.getMaxHealth());
                player.removeAllEffects();
                ItemStack halo = new ItemStack(ModItems.TIME_HALO.get());

                if (player.getMainHandItem() == clock) {
                    player.setItemInHand(InteractionHand.MAIN_HAND, halo);
                } else {
                    player.setItemInHand(InteractionHand.OFF_HAND, halo);
                }
                player.fallDistance = 0;
                BlockPos respawnPos = player.getRespawnPosition();
                ServerLevel respawnLevel = player.server.getLevel(player.getRespawnDimension());
                if (respawnLevel == null) respawnLevel = player.server.overworld();
                if (respawnPos == null) respawnPos = respawnLevel.getSharedSpawnPos();
                player.teleportTo(respawnLevel, respawnPos.getX() + 0.5, respawnPos.getY() + 1.0, respawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
                respawnLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0f, 1.0f);
                respawnLevel.sendParticles(ParticleTypes.FLASH, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.5, 0.5, 0.5, 0);
            }
        }
    }
}