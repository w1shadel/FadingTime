package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.TUTM;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class TimeDimensionEffects extends DimensionSpecialEffects {
    private static final ResourceLocation CLOCK_TEXTURE = new ResourceLocation(TUTM.MODID, "textures/misc/floating_clock.png");

    public TimeDimensionEffects() {
        super(Float.NaN, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) {
        return new Vec3(1.0, 0.95, 0.8);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float time = (float) level.getGameTime() + partialTick;
        float r = 1.0F;
        float g = 0.92F;
        float b = 0.75F;
        float pulse = Mth.sin(time * 0.05f) * 0.03f;
        RenderSystem.clearColor(r + pulse, g + pulse, b + pulse, 1.0F);
        GlStateManager._clear(16384, Minecraft.ON_OSX);
        renderClocks(level, partialTick, poseStack);
        return true;
    }

    private void renderClocks(ClientLevel level, float partialTick, PoseStack poseStack) {
        float time = (float) level.getGameTime() + partialTick;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CLOCK_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        drawClock(poseStack, time, 50, 70, 50, 20.0f);
        drawClock(poseStack, time, -60, 40, -40, 15.0f);
        drawClock(poseStack, time, 20, 100, -80, 25.0f);
        drawClock(poseStack, time, -80, 60, 90, 18.0f);
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawClock(PoseStack poseStack, float time, float x, float y, float z, float scale) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 0.3f));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(Mth.sin(time * 0.05f) * 10f));
        poseStack.scale(scale, scale, 1.0f);
        Matrix4f matrix = poseStack.last().pose();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(matrix, -1.0F, -1.0F, 0.0F).uv(0.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix, 1.0F, -1.0F, 0.0F).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(matrix, 1.0F, 1.0F, 0.0F).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex(matrix, -1.0F, 1.0F, 0.0F).uv(0.0F, 1.0F).endVertex();
        tesselator.end();
        poseStack.popPose();
    }
}