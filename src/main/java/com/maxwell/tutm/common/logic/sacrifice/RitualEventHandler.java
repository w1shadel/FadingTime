package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class RitualEventHandler {

    private static final double VOID_Y = 0.0;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;

        ServerLevel level = (ServerLevel) event.level;
        long gameTime = level.getGameTime();
        if (gameTime % 5 != 0) return;

        ResourceKey<Level> dim = level.dimension();
        if (dim == TUTMDimensions.TIME_REALM_LEVEL_KEY && gameTime % 20 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, 666.5, 10, 666.5, 50, 2.0, 30.0, 2.0, 0.05);
        }
        for (Player player : level.players()) {
            AABB searchArea = player.getBoundingBox().inflate(100.0); 
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchArea);

            for (ItemEntity item : items) {
                processItemRitual(level, item, dim);
            }
        }
        if (dim == TUTMDimensions.TIME_REALM_LEVEL_KEY) {
            AABB ritualArea = new AABB(666, -100, 666, 667, 100, 667).inflate(40.0);
            for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, ritualArea)) {
                processItemRitual(level, item, dim);
            }
        }
    }

    private static void processItemRitual(ServerLevel level, ItemEntity item, ResourceKey<Level> dim) {
        ItemStack stack = item.getItem();
        if (item.getPersistentData().getBoolean("tutm_processed")) return;
        if (dim == Level.NETHER && stack.is(ModItems.LUNAR_CHRONO_CLOCK_CHARGED.get())) {
            if (item.getY() > 120) {
                item.setDeltaMovement(0, 0.2, 0);
                item.setNoGravity(true);
                if (item.getY() > 125) {
                    level.explode(null, item.getX(), item.getY(), item.getZ(), 2.0f, Level.ExplosionInteraction.NONE);
                    spawnTransformation(level, item, new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_NETHER.get()), item.getY());
                }
            }
        }
        if (dim == Level.END && item.getY() <= VOID_Y) {
            ItemStack result = ItemStack.EMPTY;
            if (stack.is(Items.SAND)) {
                result = new ItemStack(ModItems.TIME_SAND_DUST.get(), stack.getCount());
            } else if (stack.is(ModItems.TIME_SAND_DUST.get())) {
                result = new ItemStack(ModItems.TIME_SAND.get(), stack.getCount());
            } else if (stack.is(ModItems.LUNAR_CHRONO_CLOCK_NETHER.get())) {
                result = new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_END.get());
            }

            if (!result.isEmpty()) {
                spawnTransformation(level, item, result, 100.0);
            }
        }
        if (dim == TUTMDimensions.TIME_REALM_LEVEL_KEY && item.getY() <= 10) {

            double dx = item.getX() - 666.5;
            double dz = item.getZ() - 666.5;
            double distSq = dx * dx + dz * dz;
            if (distSq < 1024 && stack.is(ModItems.LUNAR_CHRONO_CLOCK_END.get())) {
                spawnTransformation(level, item, new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_TIME.get()), 80.0);
                level.sendParticles(ParticleTypes.FLASH, 666.5, 70, 666.5, 1, 0, 0, 0, 0);
            }
        }
    }

    private static void spawnTransformation(ServerLevel level, ItemEntity oldItem, ItemStack result, double spawnY) {
        oldItem.getPersistentData().putBoolean("tutm_processed", true);
        oldItem.discard();

        ItemEntity newItem = new ItemEntity(level, oldItem.getX(), spawnY, oldItem.getZ(), result);
        newItem.setDeltaMovement(0, 0.5, 0);
        newItem.setInvulnerable(true); 
        level.addFreshEntity(newItem);
    }
    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            ItemStack stack = itemEntity.getItem();
            ServerLevel level = (ServerLevel) itemEntity.level();
            if (stack.is(ModItems.LUNAR_CHRONO_CLOCK.get()) &&
                    !level.isDay() &&
                    level.dimension() == Level.OVERWORLD) {
                if (HaloPartHelper.getCollectedCount(stack) >= 8) {
                    itemEntity.getPersistentData().putBoolean("tutm_processed", true);
                    itemEntity.discard();
                    ItemStack chargedStack = new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_CHARGED.get());
                    ItemEntity newItem = new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), chargedStack);
                    newItem.setInvulnerable(true);
                    newItem.setPickUpDelay(10);
                    level.addFreshEntity(newItem);
                    level.explode(null, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), 0.0f, Level.ExplosionInteraction.NONE);
                }
            }
        }
    }
}