package com.maxwell.tutm.common.util;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TimeHaloTooltipClient {
    public static void appendClientTooltip(List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            tooltip.add(Component.literal(""));
            TooltipHelper.addNumberedLines(tooltip, "tooltip.time_halo.lore_deep");
        } else {
            tooltip.add(Component.translatable("tooltip.tutm.hold_shift").withStyle(ChatFormatting.DARK_GRAY));
        }
        if (Screen.hasAltDown()) {
            tooltip.add(Component.literal(""));
            for (int i = 1; i <= 5; i++) {
                tooltip.add(Component.translatable("tooltip.time_halo.ability." + i));
            }
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                    tooltip.add(Component.translatable("tooltip.time_halo.ability.6", String.format("%.1f", data.attackBonus)));
                    tooltip.add(Component.translatable("tooltip.time_halo.ability.7", String.format("%.1f", data.defenseBonus)));
                });
            }
            for (int i = 8; i <= 20; i++) {
                tooltip.add(Component.translatable("tooltip.time_halo.ability." + i));
            }
        } else {
            tooltip.add(Component.translatable("tooltip.tutm.hold_alt").withStyle(ChatFormatting.DARK_AQUA));
        }
    }
}