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
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

@SuppressWarnings("removal")
public class TimeDimensionEffects extends DimensionSpecialEffects {
    private static final ResourceLocation CLOCK_TEXTURE = new ResourceLocation(TUTM.MODID, "textures/misc/floating_clock.png");

    public TimeDimensionEffects() {
        super(Float.NaN, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) {

        return new Vec3(1.0, 0.85, 0.5);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float time = (float) level.getGameTime() + partialTick;

        float r = 0.10F;
        float g = 0.05F;
        float b = 0.02F;
        RenderSystem.clearColor(r, g, b, 1.0F);
        GlStateManager._clear(16384, Minecraft.ON_OSX);

        RenderSystem.depthMask(false);

        renderDynamicStars(poseStack, time);

        renderCosmicRiver(poseStack, time);

        renderClocks(level, partialTick, poseStack);

        RenderSystem.depthMask(true);
        return true;
    }

    private void renderDynamicStars(PoseStack poseStack, float time) {
        RandomSource random = RandomSource.create(10842L); 
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        Matrix4f matrix = poseStack.last().pose();

        for (int i = 0; i < 2000; i++) {
            float x = random.nextFloat() * 2.0f - 1.0f;
            float y = random.nextFloat() * 2.0f - 1.0f;
            float z = random.nextFloat() * 2.0f - 1.0f;
            float len = (float) Math.sqrt(x*x + y*y + z*z);
            if (len < 0.01f || len > 1.0f) continue;
            x /= len; y /= len; z /= len; 
            
            float dist = 100.0f;
            x *= dist; y *= dist; z *= dist;
            
            float size = 0.15f + random.nextFloat() * 0.4f;

            float twinkle = (float) Math.sin(time * 0.05f + i * 0.1f) * 0.5f + 0.5f;
            float alpha = 0.2f + twinkle * 0.8f;

            float rCol = 1.0f;
            float gCol = 0.8f + random.nextFloat() * 0.2f;
            float bCol = 0.4f + random.nextFloat() * 0.6f;

            builder.vertex(matrix, x-size, y, z-size).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x-size, y, z+size).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x+size, y, z+size).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x+size, y, z-size).color(rCol, gCol, bCol, alpha).endVertex();
            
            builder.vertex(matrix, x-size, y-size, z).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x+size, y-size, z).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x+size, y+size, z).color(rCol, gCol, bCol, alpha).endVertex();
            builder.vertex(matrix, x-size, y+size, z).color(rCol, gCol, bCol, alpha).endVertex();
        }
        tesselator.end();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void renderCosmicRiver(PoseStack poseStack, float time) {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        poseStack.pushPose();

        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 0.03f));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(35f)); 

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f matrix = poseStack.last().pose();

        int segments = 120;
        float radius = 90.0f;
        float riverWidth = 45.0f;

        for (int i = 0; i < segments; i++) {
            float angle1 = (float) i / segments * (float) Math.PI * 2.0f;
            float angle2 = (float) (i + 1) / segments * (float) Math.PI * 2.0f;

            float w1 = (float) Math.sin(angle1 * 5 + time * 0.02f) * 20.0f + (float) Math.cos(angle1 * 2 - time * 0.01f) * 10.0f;
            float w2 = (float) Math.sin(angle2 * 5 + time * 0.02f) * 20.0f + (float) Math.cos(angle2 * 2 - time * 0.01f) * 10.0f;
            
            float x1 = (float) Math.cos(angle1) * radius;
            float z1 = (float) Math.sin(angle1) * radius;
            float x2 = (float) Math.cos(angle2) * radius;
            float z2 = (float) Math.sin(angle2) * radius;

            float rBase = 1.0f;
            float gBase = 0.7f + (float) Math.sin(angle1 * 4 + time * 0.04f) * 0.3f;
            float bBase = 0.3f + (float) Math.cos(angle1 * 3 + time * 0.03f) * 0.4f;

            float aEdge = 0.0f;
            float aMid = 0.25f + (float) Math.sin(time * 0.05f + angle1) * 0.1f;

            builder.vertex(matrix, x1, w1 - riverWidth, z1).color(rBase, gBase, bBase, aEdge).endVertex();
            builder.vertex(matrix, x2, w2 - riverWidth, z2).color(rBase, gBase, bBase, aEdge).endVertex();
            builder.vertex(matrix, x2, w2, z2).color(rBase, gBase, bBase, aMid).endVertex();
            builder.vertex(matrix, x1, w1, z1).color(rBase, gBase, bBase, aMid).endVertex();

            builder.vertex(matrix, x1, w1, z1).color(rBase, gBase, bBase, aMid).endVertex();
            builder.vertex(matrix, x2, w2, z2).color(rBase, gBase, bBase, aMid).endVertex();
            builder.vertex(matrix, x2, w2 + riverWidth, z2).color(rBase, gBase, bBase, aEdge).endVertex();
            builder.vertex(matrix, x1, w1 + riverWidth, z1).color(rBase, gBase, bBase, aEdge).endVertex();

            float coreWidth = 10.0f;
            float aCore = 0.6f + (float) Math.sin(time * 0.1f) * 0.2f;
            builder.vertex(matrix, x1, w1 - coreWidth, z1).color(1.0f, 0.95f, 0.8f, 0.0f).endVertex();
            builder.vertex(matrix, x2, w2 - coreWidth, z2).color(1.0f, 0.95f, 0.8f, 0.0f).endVertex();
            builder.vertex(matrix, x2, w2 + coreWidth, z2).color(1.0f, 0.95f, 0.8f, aCore).endVertex();
            builder.vertex(matrix, x1, w1 + coreWidth, z1).color(1.0f, 0.95f, 0.8f, aCore).endVertex();
        }
        tesselator.end();
        poseStack.popPose();
        
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
    }

    private void renderClocks(ClientLevel level, float partialTick, PoseStack poseStack) {
        float time = (float) level.getGameTime() + partialTick;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CLOCK_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        poseStack.pushPose();
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 0.1f));

        drawClock(poseStack, time, 50, 70, 50, 20.0f);
        drawClock(poseStack, time, -60, 40, -40, 15.0f);
        drawClock(poseStack, time, 20, 100, -80, 25.0f);
        drawClock(poseStack, time, -80, 60, 90, 18.0f);

        poseStack.popPose();

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