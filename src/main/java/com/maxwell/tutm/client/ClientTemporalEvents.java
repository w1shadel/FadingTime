package com.maxwell.tutm.client;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.maxwell.tutm.client.screen.TimeRadialScreen;
import com.maxwell.tutm.common.items.TimeHaloItem;
import com.maxwell.tutm.common.network.C2SExecuteSkillPacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.common.util.CurioUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

@Mod.EventBusSubscriber(modid = TUTM.MODID, value = Dist.CLIENT)
public class ClientTemporalEvents {
    public static float clientTime = 0.0f;
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TimeRenderHandler.updateClientEffects();
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;
        if (!CurioUtil.hasAnyTimeItem(mc.player)) return;
        if (ClientModEvents.EXECUTE_KEY.consumeClick()) {
            TUTMPacketHandler.INSTANCE.sendToServer(new C2SExecuteSkillPacket(1));
        }
    }
    @SubscribeEvent
    public static void onRenderTooltipPre(RenderTooltipEvent.Pre event) {
        if (event.getItemStack().getItem() instanceof TimeHaloItem) {
            int width = 0;
            int height = 0;
            for (ClientTooltipComponent component : event.getComponents()) {
                width = Math.max(width, component.getWidth(event.getFont()));
                height += component.getHeight();
            }

            width += 10;
            height += 10;
            renderGeometry(event.getGraphics().pose(), event.getX(), event.getY(), width, height);
        }
    }
    @SubscribeEvent
    public static void onRenderTooltipColor(RenderTooltipEvent.Color event) {
        if (event.getItemStack().getItem() instanceof TimeHaloItem) {
            event.setBackgroundStart(0x80000000);
            event.setBackgroundEnd(0x80000000);
        }
    }
    @SubscribeEvent
    public static void onClientTick_Count(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            clientTime += 0.15f; 
        }
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.screen != null && !(mc.screen instanceof TimeRadialScreen)) return;

        long window = mc.getWindow().getWindow();
        int keyCode = ClientModEvents.SELECT_KEY.getKey().getValue();
        boolean isPhysicallyDown = InputConstants.isKeyDown(window, keyCode);
        if (isPhysicallyDown) {
            if (!(mc.screen instanceof TimeRadialScreen) && CurioUtil.hasAnyTimeItem(mc.player)) {
                mc.setScreen(new TimeRadialScreen());
            }
        } else {
            if (mc.screen instanceof TimeRadialScreen) {
                mc.setScreen(null);
            }
        }
    }
    public static void renderGeometry(PoseStack poseStack, int x, int y, int width, int height) {
        float time = clientTime;
        float centerX = x + width / 2.0f;
        float centerY = y + height / 2.0f;
        float radius = Math.min(width, height) * 1.2f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f matrix = poseStack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance().getBuilder();

        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        for (int i = 0; i < 60; i++) {
            float rad = (float) Math.toRadians(i * 6.0f);
            float r = (i % 5 == 0) ? radius : radius * 0.9f;
            float rOut = radius * 1.05f;
            buffer.vertex(matrix, centerX + (float)Math.cos(rad) * r, centerY + (float)Math.sin(rad) * r, 200).color(255, 215, 0, 200).endVertex();
            buffer.vertex(matrix, centerX + (float)Math.cos(rad) * rOut, centerY + (float)Math.sin(rad) * rOut, 200).color(255, 255, 255, 255).endVertex();
        }


        drawClockNeedle(buffer, matrix, centerX, centerY, time * 2.0f, radius * 0.8f, 255, 255, 255, 255); 
        drawClockNeedle(buffer, matrix, centerX, centerY, time * 0.5f, radius * 0.6f, 255, 215, 0, 255);   

        for (int i = 0; i < 16; i++) {
            float rot = time * 2.0f + (i * 22.5f);
            float rad = (float)Math.toRadians(rot);
            float d = radius * 1.3f + (float)Math.sin(time * 0.5f) * 10.0f;
            float ox = centerX + (float)Math.cos(rad) * d;
            float oy = centerY + (float)Math.sin(rad) * d;

            buffer.vertex(matrix, ox, oy, 200).color(255, 255, 255, 255).endVertex();
            buffer.vertex(matrix, ox + (float)Math.cos(rad) * 20, oy + (float)Math.sin(rad) * 20, 200).color(255, 100, 0, 0).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());

        buffer.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, centerX, centerY, 190).color(255, 215, 0, 80).endVertex();
        for (int i = 0; i <= 72; i++) {
            float rad = (float) Math.toRadians(time * -5.0f + i * 5.0f);
            float d = radius * (1.2f + (float)Math.sin(time * 0.2f + i) * 0.1f);
            buffer.vertex(matrix, centerX + (float)Math.cos(rad) * d, centerY + (float)Math.sin(rad) * d, 190).color(255, 200, 50, 0).endVertex();
        }
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
    }

    private static void drawClockNeedle(BufferBuilder b, Matrix4f m, float cx, float cy, float angle, float len, int r, int g, int b_col, int a) {
        for (int offset = -1; offset <= 1; offset++) {
            float rad = (float) Math.toRadians(angle - 90 + offset * 0.5f);
            b.vertex(m, cx, cy, 200).color(r, g, b_col, a).endVertex();
            b.vertex(m, cx + (float)Math.cos(rad) * len, cy + (float)Math.sin(rad) * len, 200).color(r, g, b_col, a).endVertex();
        }
    }
}