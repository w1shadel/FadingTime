package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class RitualEventHandler {
    private static final double VOID_Y = -60.0;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide) return;
        ServerLevel level = (ServerLevel) event.level;
        AABB searchArea = new AABB(
                -3.0E7, -64, -3.0E7,
                3.0E7, 320, 3.0E7
        );
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, searchArea)) {
            ItemStack stack = item.getItem();
            if (item.getPersistentData().getBoolean("tutm_processed")) continue;
            if (level.dimension() == Level.NETHER && stack.is(ModItems.LUNAR_CHRONO_CLOCK_CHARGED.get())) {
                if (item.getY() > 120) {
                    item.setDeltaMovement(0, 0.1, 0);
                    item.setNoGravity(true);
                    if (item.getY() > 125) {
                        level.explode(null, item.getX(), item.getY(), item.getZ(), 2.0f, Level.ExplosionInteraction.NONE);
                        spawnTransformation(level, item, new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_NETHER.get()), item.getY());
                        continue;
                    }
                }
            }
            if (level.dimension() == Level.END && item.getY() <= VOID_Y) {
                if (stack.is(Items.SAND)) {
                    spawnTransformation(level, item, new ItemStack(ModItems.TIME_SAND_DUST.get(), stack.getCount()), VOID_Y + 15);
                } else if (stack.is(ModItems.TIME_SAND_DUST.get())) {
                    spawnTransformation(level, item, new ItemStack(ModItems.TIME_SAND.get(), stack.getCount()), VOID_Y + 15);
                } else if (stack.is(ModItems.LUNAR_CHRONO_CLOCK_NETHER.get())) {
                    spawnTransformation(level, item, new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_END.get()), 100);
                }
                continue;
            }
            if (level.dimension() == TUTMDimensions.TIME_REALM_LEVEL_KEY && item.getY() <= 0) {
                double distSq = item.blockPosition().distSqr(new Vec3i(666, 66, 666));
                if (distSq < 100 && stack.is(ModItems.LUNAR_CHRONO_CLOCK_END.get())) {
                    spawnTransformation(level, item, new ItemStack(ModItems.LUNAR_CHRONO_CLOCK_TIME.get()), 70);
                }
            }
        }
    }

    private static void spawnTransformation(ServerLevel level, ItemEntity oldItem, ItemStack result, double spawnY) {
        // 元のアイテムに「処理済み」のタグを付けて消去
        oldItem.getPersistentData().putBoolean("tutm_processed", true);
        oldItem.discard();
        // 新しいアイテムを生成
        ItemEntity newItem = new ItemEntity(level, oldItem.getX(), spawnY, oldItem.getZ(), result);
        newItem.setDeltaMovement(0, 0.5, 0);
        level.addFreshEntity(newItem);
    }
}