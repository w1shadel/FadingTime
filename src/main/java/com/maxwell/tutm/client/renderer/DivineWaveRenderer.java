package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.DivineWaveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

@SuppressWarnings("removal")
public class DivineWaveRenderer extends EntityRenderer<DivineWaveEntity> {
    public DivineWaveRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DivineWaveEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        int age = entity.getWaveAge();
        float rawRadius = entity.getCurrentRadius();
        float radius = rawRadius + (rawRadius > 0 ? (1.0f / DivineWaveEntity.getExpandTicks() * DivineWaveEntity.getMaxRadius()) * partialTick : 0);
        radius = Math.min(radius, DivineWaveEntity.getMaxRadius());
        float alpha;
        if (age < DivineWaveEntity.getExpandTicks()) {
            alpha = 0.35f;
        } else {
            float fadeProgress = (float) (age - DivineWaveEntity.getExpandTicks()) / DivineWaveEntity.getFadeTicks();
            alpha = 0.35f * (1.0f - fadeProgress);
        }
        if (alpha <= 0) return;
        float r = 0.95f, g = 0.97f, b = 1.0f;
        float rInner = 0.7f, gInner = 0.85f, bInner = 1.0f;
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        Matrix4f mat = pose.last().pose();
        int rings = 16;
        float thickness = 0.55f;
        for (int lat = 0; lat < rings; lat++) {
            float phi = (float) (Math.PI * lat / rings);
            float y = Mth.cos(phi) * radius;
            float rLat = Mth.sin(phi) * radius;
            float a = alpha * (0.5f + 0.5f * Mth.sin(phi));
            drawRing(vc, mat, rLat, y, 32, thickness, r, g, b, a);
        }
        for (int lon = 0; lon < rings / 2; lon++) {
            float theta = (float) (Math.PI * lon / (rings / 2));
            drawTiltedRing(vc, mat, radius, 32, thickness * 0.6f, theta, rInner, gInner, bInner, alpha * 0.4f);
        }
    }

    private void drawRing(VertexConsumer vc, Matrix4f mat,
                          float ringRadius, float yOffset,
                          int segments, float thickness,
                          float r, float g, float b, float a) {
        if (ringRadius < 0.01f) return;
        float step = (float) (Math.PI * 2.0 / segments);
        float inner = ringRadius - thickness;
        for (int i = 0; i < segments; i++) {
            float a1 = i * step, a2 = (i + 1) * step;
            float x1o = Mth.cos(a1) * ringRadius, z1o = Mth.sin(a1) * ringRadius;
            float x2o = Mth.cos(a2) * ringRadius, z2o = Mth.sin(a2) * ringRadius;
            float x1i = Mth.cos(a1) * inner, z1i = Mth.sin(a1) * inner;
            float x2i = Mth.cos(a2) * inner, z2i = Mth.sin(a2) * inner;
            drawQuad(vc, mat,
                    x1i, yOffset + thickness * 0.5f, z1i,
                    x2i, yOffset + thickness * 0.5f, z2i,
                    x2o, yOffset + thickness * 0.5f, z2o,
                    x1o, yOffset + thickness * 0.5f, z1o,
                    r, g, b, a);
            drawQuad(vc, mat,
                    x1o, yOffset - thickness * 0.5f, z1o,
                    x2o, yOffset - thickness * 0.5f, z2o,
                    x2i, yOffset - thickness * 0.5f, z2i,
                    x1i, yOffset - thickness * 0.5f, z1i,
                    r, g, b, a);
        }
    }

    private void drawTiltedRing(VertexConsumer vc, Matrix4f mat,
                                float radius, int segments, float thickness,
                                float tiltAngle,
                                float r, float g, float b, float a) {
        float step = (float) (Math.PI * 2.0 / segments);
        float inner = radius - thickness;
        float cosT = Mth.cos(tiltAngle), sinT = Mth.sin(tiltAngle);
        for (int i = 0; i < segments; i++) {
            float a1 = i * step, a2 = (i + 1) * step;
            float x1o = Mth.cos(a1) * radius, y1o = Mth.sin(a1) * radius;
            float x2o = Mth.cos(a2) * radius, y2o = Mth.sin(a2) * radius;
            float x1i = Mth.cos(a1) * inner, y1i = Mth.sin(a1) * inner;
            float x2i = Mth.cos(a2) * inner, y2i = Mth.sin(a2) * inner;
            drawQuad(vc, mat,
                    x1i * cosT, y1i, x1i * -sinT,
                    x2i * cosT, y2i, x2i * -sinT,
                    x2o * cosT, y2o, x2o * -sinT,
                    x1o * cosT, y1o, x1o * -sinT,
                    r, g, b, a);
        }
    }

    private void drawQuad(VertexConsumer vc, Matrix4f mat,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float r, float g, float b, float a) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(DivineWaveEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
