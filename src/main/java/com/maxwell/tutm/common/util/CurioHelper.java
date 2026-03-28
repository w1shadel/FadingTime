package com.maxwell.tutm.common.util;

import com.maxwell.tutm.common.items.ChronoTankItem;
import com.maxwell.tutm.common.items.TimeItem;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

@SuppressWarnings("removal")
public class CurioHelper {
    public static int getEquippedTankTier(LivingEntity entity) {
        List<SlotResult> results = CuriosApi.getCuriosHelper().findCurios(entity, "chrono_tank");
        for (SlotResult result : results) {
            if (result.stack().getItem() instanceof ChronoTankItem tank) {
                return tank.getTier();
            }
        }
        return 0;
    }
    public static boolean hasTimeItem(LivingEntity entity) {
        List<SlotResult> results = CuriosApi.getCuriosHelper().findCurios(entity, "time_slot");
        for (SlotResult result : results) {
            if (result.stack().getItem() instanceof TimeItem tank) {
               return true;
            }
        }
        return false;
    }
}