package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.logic.sacrifice.HaloPartHelper;
import com.maxwell.tutm.common.logic.sacrifice.SacrificeManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LunaChronosItem extends TimeItem {
    public LunaChronosItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (level != null) {
            int currentPhase = level.getMoonPhase();

            // 1. 基本情報
            tooltip.add(Component.translatable("tooltip.tutm.moon_phase", currentPhase).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.tutm.parts_collected", HaloPartHelper.getCollectedCount(stack)).withStyle(ChatFormatting.GOLD));

            // 2. 現在のヒント (未入手パーツがある場合のみ)
            if (!HaloPartHelper.hasPart(stack, currentPhase)) {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("tooltip.tutm.hint_header").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
                tooltip.add(Component.translatable(SacrificeManager.getHintForPhase(currentPhase)).withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.literal(""));
                tooltip.add(Component.translatable("tooltip.tutm.hint_completed").withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }
}
