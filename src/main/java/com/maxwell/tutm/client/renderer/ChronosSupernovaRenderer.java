package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.ChronosSupernovaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

public class ChronosSupernovaRenderer extends EntityRenderer<ChronosSupernovaEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("tutm", "textures/entity/supernova.png");

    public ChronosSupernovaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ChronosSupernovaEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(ChronosSupernovaEntity entity, float entityYaw, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        pose.pushPose();
        int age = entity.getEntityAge();
        float chargeTime = ChronosSupernovaEntity.CHARGE_TIME;
        float explosionTime = ChronosSupernovaEntity.EXPLOSION_TIME;
        float scale = 0.0f;
        float alpha = 1.0f;
        float r = 1.0f, g = 0.8f, b = 0.2f;
        if (age < chargeTime) {
            float deployProgress = Math.min(1.0f, age / 20.0f);
            scale = Mth.lerp(deployProgress, 0.5f, 64.0f) + (float) Math.sin(age * 0.2) * 1.5f;
            r = Mth.lerp(deployProgress, 0.5f, 1.0f);
            g = Mth.lerp(deployProgress, 1.0f, 0.2f);
            b = Mth.lerp(deployProgress, 1.0f, 0.0f);
            alpha = 0.7f + (float) Math.sin(age * 0.5) * 0.1f;
        } else {
            float explosionProgress = (age - chargeTime) / explosionTime;
            scale = 64.0f + explosionProgress * 16.0f;
            alpha = Mth.clamp(1.0f - explosionProgress, 0.0f, 1.0f);
            r = 1.0f;
            g = 1.0f;
            b = 1.0f;
        }
        pose.scale(scale, scale, scale);
        pose.mulPose(Axis.YP.rotationDegrees(age * 5.0f));
        pose.mulPose(Axis.ZP.rotationDegrees(age * 3.0f));
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        Matrix4f mat = pose.last().pose();
        renderSolidSphere(builder, mat, 1.0f, 16, 16, r, g, b, alpha);
        pose.popPose();
        super.render(entity, entityYaw, partialTicks, pose, buffer, packedLight);
    }

    private void renderSolidSphere(VertexConsumer builder, Matrix4f mat, float radius, int slices, int stacks, float r, float g, float b, float a) {
        for (int i = 0; i < stacks; i++) {
            float phi1 = (float) Math.PI * i / stacks;
            float phi2 = (float) Math.PI * (i + 1) / stacks;
            for (int j = 0; j < slices; j++) {
                float theta1 = (float) (2.0 * Math.PI * j / slices);
                float theta2 = (float) (2.0 * Math.PI * (j + 1) / slices);
                float x1 = radius * Mth.sin(phi1) * Mth.cos(theta1);
                float y1 = radius * Mth.cos(phi1);
                float z1 = radius * Mth.sin(phi1) * Mth.sin(theta1);
                float x2 = radius * Mth.sin(phi2) * Mth.cos(theta1);
                float y2 = radius * Mth.cos(phi2);
                float z2 = radius * Mth.sin(phi2) * Mth.sin(theta1);
                float x3 = radius * Mth.sin(phi2) * Mth.cos(theta2);
                float y3 = radius * Mth.cos(phi2);
                float z3 = radius * Mth.sin(phi2) * Mth.sin(theta2);
                float x4 = radius * Mth.sin(phi1) * Mth.cos(theta2);
                float y4 = radius * Mth.cos(phi1);
                float z4 = radius * Mth.sin(phi1) * Mth.sin(theta2);
                builder.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240, 240).normal(x1, y1, z1).endVertex();
                builder.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240, 240).normal(x2, y2, z2).endVertex();
                builder.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240, 240).normal(x3, y3, z3).endVertex();
                builder.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240, 240).normal(x4, y4, z4).endVertex();
            }
        }
    }
}
