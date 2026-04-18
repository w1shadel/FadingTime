package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.TemporalHomingEntity;
import com.maxwell.tutm.common.logic.TimeManager;
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

@SuppressWarnings("removal")
public class TemporalHomingRenderer extends EntityRenderer<TemporalHomingEntity> {
    public TemporalHomingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TemporalHomingEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        float pt = TimeManager.isTimeStopped() ? 0.0F : partialTick;
        float age = (float) entity.getBallAge() + pt;
        pose.pushPose();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        Matrix4f mat = pose.last().pose();
        drawSphere(vc, mat, 0.4f, 16, 0.8f, 0.4f, 1.0f, 0.8f);
        pose.mulPose(Axis.XP.rotationDegrees(age * 3.0f));
        drawRing(vc, mat, 0.6f, 6, 0.05f, age * 2.0f, 1.0f, 1.0f, 1.0f, 0.6f);
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(age * 5.0f));
        drawRing(vc, mat, 0.7f, 8, 0.03f, -age * 4.0f, 0.7f, 0.9f, 1.0f, 0.4f);
        pose.popPose();
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private void drawSphere(VertexConsumer vc, Matrix4f mat, float radius, int rings, float r, float g, float b, float a) {
        for (int i = 0; i < rings; i++) {
            float phi1 = (float) (Math.PI * i / rings);
            float phi2 = (float) (Math.PI * (i + 1) / rings);
            for (int j = 0; j < rings * 2; j++) {
                float theta1 = (float) (Math.PI * j / rings);
                float theta2 = (float) (Math.PI * (j + 1) / rings);
                float x1 = radius * Mth.sin(phi1) * Mth.cos(theta1);
                float y1 = radius * Mth.cos(phi1);
                float z1 = radius * Mth.sin(phi1) * Mth.sin(theta1);
                float x2 = radius * Mth.sin(phi1) * Mth.cos(theta2);
                float y2 = radius * Mth.cos(phi1);
                float z2 = radius * Mth.sin(phi1) * Mth.sin(theta2);
                float x3 = radius * Mth.sin(phi2) * Mth.cos(theta2);
                float y3 = radius * Mth.cos(phi2);
                float z3 = radius * Mth.sin(phi2) * Mth.sin(theta2);
                float x4 = radius * Mth.sin(phi2) * Mth.cos(theta1);
                float y4 = radius * Mth.cos(phi2);
                float z4 = radius * Mth.sin(phi2) * Mth.sin(theta1);
                drawQuad(vc, mat, x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4, r, g, b, a);
            }
        }
    }

    private void drawRing(VertexConsumer vc, Matrix4f mat, float radius, int sides, float thickness, float rotation, float r, float g, float b, float a) {
        float step = (float) (Math.PI * 2.0 / sides);
        float inner = radius - thickness;
        for (int i = 0; i < sides; i++) {
            float a1 = i * step + rotation, a2 = (i + 1) * step + rotation;
            float x1o = Mth.cos(a1) * radius, y1o = Mth.sin(a1) * radius;
            float x2o = Mth.cos(a2) * radius, y2o = Mth.sin(a2) * radius;
            float x1i = Mth.cos(a1) * inner, y1i = Mth.sin(a1) * inner;
            float x2i = Mth.cos(a2) * inner, y2i = Mth.sin(a2) * inner;
            drawQuad(vc, mat, x1i, y1i, 0, x2i, y2i, 0, x2o, y2o, 0, x1o, y1o, 0, r, g, b, a);
        }
    }

    private void drawQuad(VertexConsumer vc, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(TemporalHomingEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
