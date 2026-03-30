package com.maxwell.tutm.common.util;

import com.maxwell.tutm.common.items.ChronoTankItem;
import com.maxwell.tutm.common.items.TimeHaloItem;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@SuppressWarnings("removal")
public class CurioUtil {
    public static boolean hasCurio(LivingEntity entity, Item item) {
        if (entity == null || item == null) return false;
        return CuriosApi.getCuriosHelper().findFirstCurio(entity, item).isPresent();
    }
    public static ItemStack getHaloStack(Player player) {
        return CuriosApi.getCuriosHelper().findFirstCurio(player, stack -> stack.getItem() instanceof TimeHaloItem)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
    public static boolean hasHalo(LivingEntity entity) {
        return hasCurio(entity, ModItems.TIME_HALO.get());
    }

    public static int getEquippedTankTier(LivingEntity entity) {
        if (entity == null) return 0;
        if (hasHalo(entity)) return 4;
        return CuriosApi.getCuriosHelper()
                .findFirstCurio(entity, stack -> stack.getItem() instanceof ChronoTankItem)
                .map(slotResult -> ((ChronoTankItem) slotResult.stack().getItem()).getTier())
                .orElse(0);
    }

    public static boolean hasAnyTimeItem(LivingEntity entity) {
        if (entity == null) return false;
        return hasHalo(entity) || !CuriosApi.getCuriosHelper().findCurios(entity, "time_slot").isEmpty();
    }
}