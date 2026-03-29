package com.maxwell.tutm.common.util;

import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipHelper {
    public static void addNumberedLines(List<Component> tooltip, String baseKey) {
        int i = 1;
        while (true) {
            String key = baseKey + "." + i;
            if (!Component.translatable(key).getString().equals(key)) {
                tooltip.add(Component.translatable(key));
                i++;
            } else {
                break;
            }
        }
    }
}