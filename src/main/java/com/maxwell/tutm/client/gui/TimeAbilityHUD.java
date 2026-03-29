package com.maxwell.tutm.client.gui;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.util.CurioUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TimeAbilityHUD {
    public static final IGuiOverlay HUD = (gui, graphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || !CurioUtil.hasAnyTimeItem(mc.player)) return;
        mc.player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            int baseX = width - 50;
            int baseY = height - 50;
            renderSkillIcon(graphics, 1, data.selectedSkill == 1, baseX + 10, baseY - 5);
            renderSkillIcon(graphics, 2, data.selectedSkill == 2, baseX - 5, baseY + 15);
            renderSkillIcon(graphics, 0, data.selectedSkill == 0, baseX + 25, baseY + 15);
        });
    };

    private static void renderSkillIcon(GuiGraphics graphics, int id, boolean selected, int x, int y) {
        RenderSystem.enableBlend();
        float alpha = selected ? 1.0F : 0.3F;
        float colorVal = selected ? 1.0F : 0.2F;
        graphics.setColor(colorVal, colorVal, colorVal, alpha);
        graphics.blit(TimeTextures.SLOT, x, y, 0, 0, 18, 18, 18, 18);
        ResourceLocation icon = switch (id) {
            case 0 -> TimeTextures.ACCE;
            case 1 -> TimeTextures.STOP;
            case 2 -> TimeTextures.REWIND;
            default -> TimeTextures.STOP;
        };
        if (selected) {
            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            graphics.setColor(0.3F, 0.3F, 0.3F, 0.5F);
        }
        graphics.blit(icon, x + 1, y + 1, 0, 0, 16, 16, 16, 16);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}