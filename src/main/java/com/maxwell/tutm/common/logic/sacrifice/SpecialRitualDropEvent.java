package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.AllLunarChronoItem;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class SpecialRitualDropEvent {
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide) return;
        LivingEntity victim = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (killer instanceof Player player && isHoldingClock(player)) {
            long timeOfDay = level.dayTime() % 24000;
            boolean isDay = level.isDay();
            if (victim instanceof Cat cat) {
                ResourceLocation variantId = BuiltInRegistries.CAT_VARIANT.getKey(cat.getVariant());
                if (variantId != null) {
                    String path = variantId.getPath();
                    if (path.contains("all_black")) {
                        if (level.isDay()) {
                            dropItem(victim, ModItems.BLACK_CAT_CORPSE.get());
                        }
                    }
                }
            }
            if (victim instanceof Slime) {
                if (!isDay) {
                    dropItem(victim, ModItems.SLIME_ETHEREAL_AMALGAM.get());
                }
            }

        }
    }

    private static boolean isHoldingClock(Player player) {
        return player.getMainHandItem().getItem() instanceof AllLunarChronoItem ||
                player.getOffhandItem().getItem() instanceof AllLunarChronoItem;
    }

    private static void dropItem(LivingEntity entity, Item item) {
        entity.spawnAtLocation(new ItemStack(item));
    }
}
