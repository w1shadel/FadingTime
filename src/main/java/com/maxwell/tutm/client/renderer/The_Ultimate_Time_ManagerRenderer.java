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
import org.jetbrains.annotations.Nullable;

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
    }
    @Override
    protected void setupRotations(The_Ultimate_TimeManagerEntity entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        super.setupRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        this.model.head.xRot *= -1;
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