package com.maxwell.tutm.client.gui;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class TimeGaugeOverlay {
    private static final ResourceLocation FRAME = new ResourceLocation(TUTM.MODID, "textures/gui/time_gauge_frame.png");
    private static final ResourceLocation FILL = new ResourceLocation(TUTM.MODID, "textures/gui/time_gauge_fill.png");
    public static final IGuiOverlay HUD = (gui, guiGraphics, partialTick, width, height) -> {
        if (gui.getMinecraft().options.hideGui) return;
        int x = 10;
        int y = height / 2 - 40;
        int w = 16;
        int h = 80;
        guiGraphics.enableScissor(x, y, x + w, y + h);
        float ratio = (float) TimeManager.getClientCostRatio();
        int fillH = (int) (h * ratio);
        int offsetH = h - fillH;
        guiGraphics.blit(FILL, x, y + offsetH, 0, offsetH, w, fillH, w, h);
        guiGraphics.disableScissor();
        guiGraphics.blit(FRAME, x, y, 0, 0, w, h, w, h);
    };
}