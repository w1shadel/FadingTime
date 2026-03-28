package com.maxwell.tutm.client.gui;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.network.UpdateBossBarPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CustomBossBarManager {
    private static final ResourceLocation BOSS_BAR_TEXTURE = new ResourceLocation(TUTM.MODID, "textures/gui/tutm_health.png");
    private static final ResourceLocation BOSS_BAR_TEXTURE_UNDERHP = new ResourceLocation(TUTM.MODID, "textures/gui/tutm_health_under.png");
    private static boolean shouldDisplay = false;
    private static float currentHealth = 0;
    private static float maxHealth = 1;
    private static Component bossName = Component.empty();
    private static boolean isSecondForm = false;

    public static void handleUpdatePacket(UpdateBossBarPacket packet) {
        shouldDisplay = packet.shouldDisplay();
        if (shouldDisplay) {
            currentHealth = packet.getCurrentHealth();
            maxHealth = packet.getMaxHealth();
            bossName = packet.getBossName();
            isSecondForm = packet.isSecond();
        }
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiOverlayEvent.Post event) {
        if (!shouldDisplay || Minecraft.getInstance().options.hideGui) {
            return;
        }
        GuiGraphics guiGraphics = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        float ticks = (float) mc.level.getGameTime() + event.getPartialTick();
        int screenWidth = guiGraphics.guiWidth();
        int barWidth = 144;
        int barHeight = 16;
        int x = screenWidth / 2 - barWidth / 2;
        int y = 17;
        float healthPercent = (maxHealth > 0.0F) ? (currentHealth / maxHealth) : 0.0F;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BOSS_BAR_TEXTURE_UNDERHP, x, y, 0, 0, barWidth, barHeight, barWidth, barHeight);
        if (healthPercent > 0) {
            int progressWidth = (int) (healthPercent * (float) barWidth);
            guiGraphics.blit(BOSS_BAR_TEXTURE, x, y, 0, 0, progressWidth, barHeight, barWidth, barHeight);
            float sheenPos = (ticks * 1.5f) % (barWidth * 4);
            int sheenX = x + (int) sheenPos - barWidth;
            if (sheenX > x && sheenX < x + progressWidth) {
                int endX = Math.min(sheenX + 30, x + progressWidth);
                guiGraphics.fill(sheenX, y, endX, y + barHeight, 0x22FFFFFF);
            }
        }
        String nameStr = bossName.getString();
        int totalNameWidth = mc.font.width(nameStr);
        float startX = (screenWidth / 2f) - (totalNameWidth / 2f);
        float currentX = startX;
        for (int i = 0; i < nameStr.length(); i++) {
            String singleChar = String.valueOf(nameStr.charAt(i));
            float timeScale = ticks * 0.08f + (i * 0.5f);
            float wave1 = Mth.sin(timeScale) * 2.0f;
            float wave2 = Mth.cos(timeScale * 0.7f) * 1.2f;
            float wave3 = Mth.sin(timeScale * 1.3f + 1.5f) * 0.8f;
            float charOffY = wave1 + wave2 + wave3;
            float charOffX = Mth.cos(timeScale * 0.5f) * 0.8f;
            int charColor;
            float colorFreq = ticks * 0.15f + (i * 0.4f);
            float colorFactor = (Mth.sin(colorFreq) + 1.0f) / 2.0f;
            if (isSecondForm) {
                int r = 255;
                int g = (int) Mth.lerp(colorFactor, 150, 250);
                int b = (int) Mth.lerp(colorFactor, 0, 180);
                charColor = (r << 16) | (g << 8) | b;
                charOffX += (mc.level.random.nextFloat() - 0.5f) * 1.8f;
                charOffY *= 1.8f;
                if (mc.level.random.nextFloat() > 0.985f) {
                    charColor = 0xAA0000;
                    charOffY += (mc.level.random.nextFloat() - 0.5f) * 10.0f;
                }
            } else {
                int r = 255;
                int g = (int) Mth.lerp(colorFactor, 215, 255);
                int b = (int) Mth.lerp(colorFactor, 0, 150);
                charColor = (r << 16) | (g << 8) | b;
            }
            guiGraphics.drawString(mc.font, singleChar, (int) (currentX + charOffX), (int) (y - 12 + charOffY), charColor, true);
            currentX += mc.font.width(singleChar);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}