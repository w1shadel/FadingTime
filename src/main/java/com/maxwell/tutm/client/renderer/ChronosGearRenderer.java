package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.ChronosGearEntity;
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

public class ChronosGearRenderer extends EntityRenderer<ChronosGearEntity> {
    public ChronosGearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChronosGearEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        int state = entity.getGearState();
        float timer = entity.getStateTimer() + partialTick;
        pose.pushPose();
        float interpolatedPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        float interpolatedYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
        if (state == ChronosGearEntity.STATE_FLYING || state == ChronosGearEntity.STATE_BOUNCING) {
            pose.mulPose(Axis.YP.rotationDegrees(interpolatedYaw));
            pose.mulPose(Axis.XP.rotationDegrees(interpolatedPitch));
            pose.mulPose(Axis.YP.rotationDegrees(90.0f));
            float spinSpeed = (state == ChronosGearEntity.STATE_BOUNCING) ? 60.0f : 40.0f;
            pose.mulPose(Axis.ZP.rotationDegrees(timer * spinSpeed));
        } else {
            pose.mulPose(Axis.XP.rotationDegrees(90.0f));
            pose.mulPose(Axis.ZP.rotationDegrees(timer * 10.0f));
        }
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));
        Matrix4f mat = pose.last().pose();
        int light = (state == ChronosGearEntity.STATE_WAITING) ? packedLight : 15728880;
        float circleAlpha = 0.3f * (1.0f + Mth.sin(timer * 0.1f));
        drawGeometricCircle(vc, mat, 1.3f, 6, 0.04f, timer * 2.0f, 0.8f, 0.8f, 1.0f, circleAlpha, light);
        drawGeometricCircle(vc, mat, 1.1f, 8, 0.02f, -timer * 1.5f, 0.6f, 0.7f, 1.0f, circleAlpha * 0.6f, light);
        switch (state) {
            case ChronosGearEntity.STATE_WAITING -> renderWaiting(pose, vc, mat, timer, light);
            case ChronosGearEntity.STATE_FLYING -> renderFlying(pose, vc, mat, timer, light);
            case ChronosGearEntity.STATE_BOUNCING -> renderBouncing(pose, vc, mat, timer, light);
        }
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private void renderFlying(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer, int light) {
        float alpha = 0.7f;
        draw3DGear(vc, mat, 1.0f, 0.15f, 20, 0.4f, 0.8f, 0.8f, 0.0f, alpha, light);
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * -80.0f));
        draw3DGear(vc, pose.last().pose(), 0.8f, 0.1f, 12, 0.2f, 0.8f, 0.2f, 0.0f, alpha * 0.6f, light);
        pose.popPose();
    }

    private void renderWaiting(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer, int light) {
        float pulse = 0.5f + Mth.sin(timer * 0.15f) * 0.3f;
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * 2.0f));
        draw3DGear(vc, pose.last().pose(), 1.2f, 0.25f, 16, 0.2f, 0.7f, 0.5f, 0.1f, pulse, light);
        pose.popPose();

    }

    private void renderBouncing(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer, int light) {
        float scale = 1.0f + Mth.sin(timer * 0.4f) * 0.2f;
        float whiten = 0.4f + Mth.sin(timer * 0.4f) * 0.4f;
        pose.pushPose();
        pose.scale(scale, scale, scale);
        draw3DGear(vc, pose.last().pose(), 1.0f, 0.5f, 12, 0.2f, 0.8f, 0.6f + (whiten * 0.2f), whiten * 0.8f, 0.8f, light);
        pose.popPose();
    }

    private void draw3DGear(VertexConsumer vc, Matrix4f mat,
                            float radius, float thickness, int toothCount, float toothDepth,
                            float r, float g, float b, float a, int light) {
        float halfZ = thickness / 2.0f;
        float innerR = radius - toothDepth;
        float step = (float) (Math.PI * 2.0 / toothCount);
        for (int i = 0; i < toothCount; i++) {
            float angle = i * step;
            float nextAngle = (i + 1) * step;
            float x1_out = Mth.cos(angle + step * 0.2f) * radius;
            float y1_out = Mth.sin(angle + step * 0.2f) * radius;
            float x2_out = Mth.cos(angle + step * 0.8f) * radius;
            float y2_out = Mth.sin(angle + step * 0.8f) * radius;
            float x1_in = Mth.cos(angle) * innerR;
            float y1_in = Mth.sin(angle) * innerR;
            float x2_in = Mth.cos(nextAngle) * innerR;
            float y2_in = Mth.sin(nextAngle) * innerR;
            drawQuad(vc, mat, 0, 0, halfZ, x1_in, y1_in, halfZ, x2_in, y2_in, halfZ, 0, 0, halfZ, r, g, b, a, 0, 0, 1, light);
            drawQuad(vc, mat, x1_in, y1_in, halfZ, x1_out, y1_out, halfZ, x2_out, y2_out, halfZ, x2_in, y2_in, halfZ, r, g, b, a, 0, 0, 1, light);
            drawQuad(vc, mat, 0, 0, -halfZ, x2_in, y2_in, -halfZ, x1_in, y1_in, -halfZ, 0, 0, -halfZ, r, g, b, a, 0, 0, -1, light);
            drawQuad(vc, mat, x2_in, y2_in, -halfZ, x2_out, y2_out, -halfZ, x1_out, y1_out, -halfZ, x1_in, y1_in, -halfZ, r, g, b, a, 0, 0, -1, light);
            drawQuad(vc, mat, x1_out, y1_out, halfZ, x1_out, y1_out, -halfZ, x2_out, y2_out, -halfZ, x2_out, y2_out, halfZ, r * 0.7f, g * 0.7f, b * 0.7f, a, Mth.cos(angle), Mth.sin(angle), 0, light);
            drawQuad(vc, mat, x2_out, y2_out, halfZ, x2_out, y2_out, -halfZ, x2_in, y2_in, -halfZ, x2_in, y2_in, halfZ, r * 0.6f, g * 0.6f, b * 0.6f, a, Mth.cos(angle), Mth.sin(angle), 0, light);
            drawQuad(vc, mat, x1_in, y1_in, halfZ, x1_in, y1_in, -halfZ, x1_out, y1_out, -halfZ, x1_out, y1_out, halfZ, r * 0.8f, g * 0.8f, b * 0.8f, a, -Mth.sin(angle), Mth.cos(angle), 0, light);
        }
    }

    private void drawQuad(VertexConsumer vc, Matrix4f mat,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float r, float g, float b, float a,
                          float nx, float ny, float nz, int light) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(light).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(light).normal(nx, ny, nz).endVertex();
    }

    private void drawGeometricCircle(VertexConsumer vc, Matrix4f mat, float radius, int sides, float thickness, float rotation, float r, float g, float b, float a, int light) {
        float step = (float) (Math.PI * 2.0 / sides);
        float inner = radius - thickness;
        for (int i = 0; i < sides; i++) {
            float a1 = i * step + rotation, a2 = (i + 1) * step + rotation;
            float x1o = Mth.cos(a1) * radius, y1o = Mth.sin(a1) * radius;
            float x2o = Mth.cos(a2) * radius, y2o = Mth.sin(a2) * radius;
            float x1i = Mth.cos(a1) * inner, y1i = Mth.sin(a1) * inner;
            float x2i = Mth.cos(a2) * inner, y2i = Mth.sin(a2) * inner;
            drawQuad(vc, mat, x1i, y1i, 0, x2i, y2i, 0, x2o, y2o, 0, x1o, y1o, 0, r, g, b, a, 0, 0, 1, light);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ChronosGearEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}