package com.maxwell.tutm.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LastClockItem extends LunarTimeComponentItem implements AllLunarChronoItem {
    public LastClockItem(Properties pProperties, Component description) {
        super(pProperties, description);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        pTooltipComponents.add(Component.literal(""));
        pTooltipComponents.add(Component.translatable("item.tutm.lunar_chrono_clock_time.desc.2"));
        pTooltipComponents.add(Component.translatable("item.tutm.lunar_chrono_clock_time.desc.3"));
        pTooltipComponents.add(Component.translatable("item.tutm.lunar_chrono_clock_time.desc.4"));
        pTooltipComponents.add(Component.translatable("item.tutm.lunar_chrono_clock_time.desc.5"));
    }
}
