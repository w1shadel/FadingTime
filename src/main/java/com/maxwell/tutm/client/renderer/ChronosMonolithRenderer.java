package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.ChronosMonolithEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class ChronosMonolithRenderer extends EntityRenderer<ChronosMonolithEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/misc/white.png");

    public ChronosMonolithRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChronosMonolithEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        float timer = entity.tickCount + partialTick;
        pose.pushPose();
        VertexConsumer vcBody = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE));
        drawMonolithBody(pose, vcBody, packedLight);
        int glowLight = 15728880;
        drawDecorations(pose, vcBody, timer, glowLight);
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private void drawMonolithBody(PoseStack pose, VertexConsumer vc, int light) {
        Matrix4f mat = pose.last().pose();
        float r = 0.0f, g = 0.0f, b = 0.05f, a = 1.0f;
        float height = 5.0f;
        float width = 0.45f;
        drawQuad(vc, mat, -width, 0, -width, -width, height, -width, width, height, -width, width, 0, -width, r, g, b, a, light);
        drawQuad(vc, mat, width, 0, -width, width, height, -width, width, height, width, width, 0, width, r * 0.8f, g, b * 0.8f, a, light);
        drawQuad(vc, mat, width, 0, width, width, height, width, -width, height, width, -width, 0, width, r, g, b, a, light);
        drawQuad(vc, mat, -width, 0, width, -width, height, width, -width, height, -width, -width, 0, -width, r * 0.8f, g, b * 0.8f, a, light);
        drawQuad(vc, mat, -width, height, -width, -width, height, width, width, height, width, width, height, -width, r, g, b, a, light);
    }

    private void drawDecorations(PoseStack pose, VertexConsumer vc, float timer, int light) {
        Matrix4f mat = pose.last().pose();
        float cr = 0.0f, cg = 0.8f, cb = 1.0f;
        float pulse = 0.6f + Mth.sin(timer * 0.08f) * 0.4f;
        float w = 0.46f;
        float h = 5.0f;
        drawQuad(vc, mat, -w, 0, -w, -w, h, -w, -w + 0.05f, h, -w, -w + 0.05f, 0, -w, cr, cg, cb, pulse, light);
        drawQuad(vc, mat, w, 0, w, w, h, w, w - 0.05f, h, w, w - 0.05f, 0, w, cr, cg, cb, pulse, light);
        drawQuad(vc, mat, -w, 0, w, -w, h, w, -w + 0.05f, h, w, -w + 0.05f, 0, w, cr, cg, cb, pulse, light);
        drawQuad(vc, mat, w, 0, -w, w, h, -w, w - 0.05f, h, -w, w - 0.05f, 0, -w, cr, cg, cb, pulse, light);
        for (int i = 0; i < 5; i++) {
            pose.pushPose();
            pose.translate(0, 0.5f + i * 1.0f, 0);
            float speed = (i % 2 == 0) ? 1.0f : -1.0f;
            pose.mulPose(Axis.YP.rotationDegrees(timer * 15f * speed));
            pose.mulPose(Axis.XP.rotationDegrees(90));
            float radius = 0.8f + Mth.sin(timer * 0.05f + i) * 0.1f;
            drawCircleFrame(vc, pose.last().pose(), radius, 0.04f, cr, cg, cb, pulse * 0.8f, light);
            pose.popPose();
        }
    }

    private void drawCircleFrame(VertexConsumer vc, Matrix4f mat, float radius, float thickness, float r, float g, float b, float a, int light) {
        int sides = 24;
        float step = (float) (Math.PI * 2.0 / sides);
        for (int i = 0; i < sides; i++) {
            float a1 = i * step, a2 = (i + 1) * step;
            float x1o = Mth.cos(a1) * radius, y1o = Mth.sin(a1) * radius;
            float x2o = Mth.cos(a2) * radius, y2o = Mth.sin(a2) * radius;
            float x1i = Mth.cos(a1) * (radius - thickness), y1i = Mth.sin(a1) * (radius - thickness);
            float x2i = Mth.cos(a2) * (radius - thickness), y2i = Mth.sin(a2) * (radius - thickness);
            drawQuad(vc, mat, x1i, y1i, 0, x2i, y2i, 0, x2o, y2o, 0, x1o, y1o, 0, r, g, b, a, light);
        }
    }

    private void drawQuad(VertexConsumer vc, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a, int light) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(light).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(light).normal(0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ChronosMonolithEntity entity) {
        return TEXTURE;
    }
}