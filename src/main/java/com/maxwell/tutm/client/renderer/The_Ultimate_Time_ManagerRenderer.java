package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.tutm_entity.The_Ultimate_Time_ManagerModel;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class The_Ultimate_Time_ManagerRenderer extends MobRenderer<The_Ultimate_TimeManagerEntity, The_Ultimate_Time_ManagerModel> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(TUTM.MODID, "textures/entity/tutm.png");
    private static final ResourceLocation GLOW_TEXTURE = ResourceLocation.fromNamespaceAndPath(TUTM.MODID, "textures/entity/tutm_glow.png");

    public The_Ultimate_Time_ManagerRenderer(EntityRendererProvider.Context context) {
        super(context, new The_Ultimate_Time_ManagerModel(context.bakeLayer(The_Ultimate_Time_ManagerModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new TimeManagerGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(The_Ultimate_TimeManagerEntity entity) {
        return TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(The_Ultimate_TimeManagerEntity pEntity, boolean pInvisible, boolean pTranslucent, boolean pGlowing) {
        return super.getRenderType(pEntity, pInvisible, pTranslucent, pGlowing);
    }

    @Override
    public void render(The_Ultimate_TimeManagerEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);

        float age = (float) pEntity.tickCount + pPartialTicks;
        boolean second = pEntity.isSecondForm();
        float r = second ? 1.0f : 1.0f;
        float g = second ? 0.1f : 0.9f;
        float b = second ? 0.2f : 0.4f;

        pPoseStack.pushPose();
        // 足元のグリッドエフェクト (拡大: 1.5f -> 3.0f)
        VertexConsumer vc = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png")));
        Matrix4f mat = pPoseStack.last().pose();
        drawFootGrid(vc, mat, 3.0f, 10, age * 0.5f, r, g, b, 0.4f);

        // 背後の時計リング (拡大)
        pPoseStack.translate(0, 1.2, 0.3);
        pPoseStack.mulPose(Axis.XP.rotationDegrees(90));
        drawClockRing(vc, pPoseStack.last().pose(), 1.5f, 12, age * 2.0f, r, g, b, 0.6f);
        drawClockRing(vc, pPoseStack.last().pose(), 1.0f, 6, -age * 1.0f, r, g, b, 0.4f);
        
        pPoseStack.popPose();
    }

    private void drawFootGrid(VertexConsumer vc, Matrix4f mat, float radius, int lines, float rotation, float r, float g, float b, float a) {
        float step = radius * 2.0f / lines;
        for (int i = 0; i <= lines; i++) {
            float offset = -radius + i * step;
            // X方向の線
            drawThinLine(vc, mat, -radius, 0.01f, offset, radius, 0.01f, offset, r, g, b, a);
            // Z方向の線
            drawThinLine(vc, mat, offset, 0.01f, -radius, offset, 0.01f, radius, r, g, b, a);
        }
    }

    private void drawClockRing(VertexConsumer vc, Matrix4f mat, float radius, int sides, float rotation, float r, float g, float b, float a) {
        float step = (float) (Math.PI * 2.0 / sides);
        for (int i = 0; i < sides; i++) {
            float angle = i * step + (float) Math.toRadians(rotation);
            float inner = radius * 0.95f;
            float x1 = Mth.cos(angle) * inner, y1 = Mth.sin(angle) * inner;
            float x2 = Mth.cos(angle) * radius, y2 = Mth.sin(angle) * radius;
            drawThinLine(vc, mat, x1, y1, 0, x2, y2, 0, r, g, b, a);
        }
    }

    private void drawThinLine(VertexConsumer vc, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        float thickness = 0.02f;
        vc.vertex(mat, x1, y1 - thickness, z1).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x2, y2 - thickness, z2).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x2, y2 + thickness, z2).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
        vc.vertex(mat, x1, y1 + thickness, z1).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(0, 1, 0).endVertex();
    }
    @Override
    protected void setupRotations(The_Ultimate_TimeManagerEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
    }
    private static class TimeManagerGlowLayer extends RenderLayer<The_Ultimate_TimeManagerEntity, The_Ultimate_Time_ManagerModel> {
        public TimeManagerGlowLayer(RenderLayerParent<The_Ultimate_TimeManagerEntity, The_Ultimate_Time_ManagerModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, The_Ultimate_TimeManagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            The_Ultimate_Time_ManagerModel model = this.getParentModel();
            VertexConsumer baseConsumer = pBuffer.getBuffer(RenderType.entityTranslucent(GLOW_TEXTURE));
            boolean wasAVisible = model.effectA.visible;
            boolean wasBVisible = model.effectB.visible;
            model.effectA.visible = false;
            model.effectB.visible = false;
            model.renderToBuffer(pPoseStack, baseConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer effectConsumer = pBuffer.getBuffer(RenderType.entityTranslucent(ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/white.png")));
            model.effectA.visible = wasAVisible;
            if (wasAVisible) {
                model.effectA.render(pPoseStack, effectConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 0.9F, 0.1F, 0.5F);
            }
            model.effectB.visible = wasBVisible;
            if (wasBVisible) {
                model.effectB.render(pPoseStack, effectConsumer, 15728880, OverlayTexture.NO_OVERLAY, 0.8F, 0.85F, 0.9F, 0.4F);
            }
        }
    }
}