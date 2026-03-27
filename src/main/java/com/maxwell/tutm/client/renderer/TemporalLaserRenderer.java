package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.TemporalLaserEntity;
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
public class TemporalLaserRenderer extends EntityRenderer<TemporalLaserEntity> {
    public TemporalLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TemporalLaserEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        float pt = TimeManager.isTimeStopped() ? 0.0F : partialTick;
        float age = (float) entity.getLaserAge() + pt;
        pose.pushPose();
        float yaw = Mth.lerp(pt, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(pt, entity.xRotO, entity.getXRot());
        pose.mulPose(Axis.YP.rotationDegrees(-yaw));
        pose.mulPose(Axis.XP.rotationDegrees(pitch));
        if (TimeManager.isTimeStopped()) {
            pose.translate(
                    (entity.level().random.nextFloat() - 0.5f) * 0.05f,
                    (entity.level().random.nextFloat() - 0.5f) * 0.05f,
                    0
            );
        }
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        Matrix4f mat = pose.last().pose();
        float baseRadius = 1.0F;
        if (age < TemporalLaserEntity.CHARGE_TIME) {
            float progress = age / (float) TemporalLaserEntity.CHARGE_TIME;
            float easeProgress = Mth.sin(progress * (float) Math.PI / 2);
            float alpha = 0.3F + Mth.sin(age * 0.8F) * 0.4F;
            if (TimeManager.isTimeStopped()) alpha = 0.8F;
            renderHollowPolygon(builder, mat, 1.5F, baseRadius, 5, 0.1F, alpha, age * 2.0F);
            float distB = Mth.lerp(easeProgress, 1.5F, 3.0F);
            float radB = Mth.lerp(easeProgress, baseRadius, baseRadius * 1.8F);
            renderHollowPolygon(builder, mat, distB, radB, 6, 0.12F, alpha, -age * 1.5F);
            float distC = Mth.lerp(easeProgress, 1.5F, 4.5F);
            float radC = Mth.lerp(easeProgress, baseRadius, baseRadius * 1.3F);
            renderHollowPolygon(builder, mat, distC, radC, 7, 0.1F, alpha, age * 1.0F);
            float distD = Mth.lerp(easeProgress, 1.5F, 6.0F);
            float radD = Mth.lerp(easeProgress, baseRadius, baseRadius * 0.8F);
            renderHollowPolygon(builder, mat, distD, radD, 8, 0.08F, alpha, -age * 0.5F);

        } else {
            float beamDuration = (float) (TemporalLaserEntity.DURATION - TemporalLaserEntity.CHARGE_TIME);
            float beamAlpha = Mth.clamp(1.0F - (age - TemporalLaserEntity.CHARGE_TIME) / beamDuration, 0, 1);
            if (beamAlpha > 0) {
                drawCrossedQuads(builder, mat, 0.8F, 64.0F, 0.7f, 0.0f, 1.0f, beamAlpha * 0.6f);
                drawCrossedQuads(builder, mat, 0.3F, 64.0F, 0.9f, 0.6f, 1.0f, beamAlpha);
            }
        }
        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    private void renderHollowPolygon(VertexConsumer builder, Matrix4f mat, float zOffset, float radius, int sides, float thickness, float alpha, float rotationZ) {
        float angleStep = (float) (Math.PI * 2.0 / sides);
        float rotRad = (float) Math.toRadians(rotationZ);
        for (int i = 0; i < sides; i++) {
            float angle1 = i * angleStep + rotRad;
            float angle2 = (i + 1) * angleStep + rotRad;
            float x1_out = Mth.cos(angle1) * radius;
            float y1_out = Mth.sin(angle1) * radius;
            float x2_out = Mth.cos(angle2) * radius;
            float y2_out = Mth.sin(angle2) * radius;
            float innerRad = radius - thickness;
            float x1_in = Mth.cos(angle1) * innerRad;
            float y1_in = Mth.sin(angle1) * innerRad;
            float x2_in = Mth.cos(angle2) * innerRad;
            float y2_in = Mth.sin(angle2) * innerRad;
            drawQuad(builder, mat,
                    x1_in, y1_in, zOffset,
                    x2_in, y2_in, zOffset,
                    x2_out, y2_out, zOffset,
                    x1_out, y1_out, zOffset,
                    0.8f, 0.2f, 1.0f, alpha);
        }
    }

    private void drawCrossedQuads(VertexConsumer builder, Matrix4f mat, float width, float length, float r, float g, float b, float a) {
        float half = width / 2.0F;
        drawQuad(builder, mat, -half, 0, 0, half, 0, 0, half, 0, length, -half, 0, length, r, g, b, a);
        drawQuad(builder, mat, 0, -half, 0, 0, half, 0, 0, half, length, 0, -half, length, r, g, b, a);
    }

    private void drawQuad(VertexConsumer builder, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a) {
        builder.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        builder.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        builder.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
        builder.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(240).normal(0, 1, 0).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(TemporalLaserEntity pEntity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}