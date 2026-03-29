package com.maxwell.tutm.client.screen;

import com.maxwell.tutm.client.gui.TimeTextures;
import com.maxwell.tutm.common.network.C2SSelectSkillPacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TimeRadialScreen extends Screen {
    private int lastSentSkill = -1;

    public TimeRadialScreen() {
        super(Component.literal("Time Radial Menu"));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + 90;
        if (angle < 0) angle += 360;
        int selected;
        if (angle >= 300 || angle < 60) {
            selected = 1;
        } else if (angle >= 60 && angle < 180) {
            selected = 0;
        } else {
            selected = 2;
        }
        if (selected != lastSentSkill) {
            updateSelectedSkill(selected);
            lastSentSkill = selected;
        }
        drawRadialIcon(graphics, TimeTextures.STOP, centerX, centerY - 50, selected == 1);
        drawRadialIcon(graphics, TimeTextures.ACCE, centerX + 40, centerY + 30, selected == 0);
        drawRadialIcon(graphics, TimeTextures.REWIND, centerX - 40, centerY + 30, selected == 2);
        String name = switch (selected) {
            case 0 -> "TIME ACCELERATION";
            case 1 -> "TIME STOP";
            case 2 -> "TIME REWINDING";
            default -> "";
        };
        graphics.drawCenteredString(this.font, name, centerX, centerY + 60, 0xFFFFFF);
    }

    private void drawRadialIcon(GuiGraphics graphics, ResourceLocation icon, int x, int y, boolean active) {
        float scale = active ? 2.5F : 1.5F;
        float alpha = active ? 1.0F : 0.3F;
        float brightness = active ? 1.0F : 0.4F;
        RenderSystem.enableBlend();
        graphics.setColor(brightness, brightness, brightness, alpha);
        graphics.pose().pushPose();
        graphics.pose().translate(x - (8 * scale), y - (8 * scale), 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.blit(icon, 0, 0, 0, 0, 16, 16, 16, 16);
        graphics.pose().popPose();
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void updateSelectedSkill(int skill) {
        TUTMPacketHandler.INSTANCE.sendToServer(new C2SSelectSkillPacket(skill));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}