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

public class LunaChronosItem extends Item implements AllLunarChronoItem {
    public LunaChronosItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        if (level != null) {
            int currentPhase = level.getMoonPhase();
            int collectedCount = HaloPartHelper.getCollectedCount(stack);

            tooltip.add(Component.translatable("tooltip.tutm.moon_phase", currentPhase).withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.tutm.parts_collected", collectedCount).withStyle(ChatFormatting.GOLD));

            tooltip.add(Component.literal(""));
            if (collectedCount < 8) {
                if (!HaloPartHelper.hasPart(stack, currentPhase)) {
                    tooltip.add(Component.translatable("tooltip.tutm.hint_header").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
                    tooltip.add(Component.translatable(SacrificeManager.getHintForPhase(currentPhase)).withStyle(ChatFormatting.GRAY));
                } else {
                    tooltip.add(Component.translatable("tooltip.tutm.hint_completed").withStyle(ChatFormatting.DARK_GREEN));
                }
            } else {
                tooltip.add(Component.translatable("tooltip.tutm.all_parts_gathered").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
                tooltip.add(Component.translatable("tooltip.tutm.lightning_hint_header").withStyle(ChatFormatting.LIGHT_PURPLE));

                tooltip.add(Component.translatable("tooltip.tutm.lightning_hint_desc").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }
}
