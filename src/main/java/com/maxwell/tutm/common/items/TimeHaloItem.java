package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.util.TimeHaloTooltipClient;
import com.maxwell.tutm.common.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class TimeHaloItem extends Item implements ICurioItem {
    public TimeHaloItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        TooltipHelper.addNumberedLines(pTooltipComponents, "tooltip.time_halo.lore_base");
        if (pLevel != null && pLevel.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TimeHaloTooltipClient.appendClientTooltip(pTooltipComponents)
            );
        }
    }
}