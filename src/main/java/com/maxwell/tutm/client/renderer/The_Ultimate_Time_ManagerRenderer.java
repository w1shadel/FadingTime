package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.tutm_entity.The_Ultimate_Time_ManagerModel;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
    private static final ResourceLocation TEXTURE = new ResourceLocation(TUTM.MODID, "textures/entity/tutm.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(TUTM.MODID, "textures/entity/tutm_glow.png");

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
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, 0);
    }
    private static class TimeManagerGlowLayer extends RenderLayer<The_Ultimate_TimeManagerEntity, The_Ultimate_Time_ManagerModel> {
        public TimeManagerGlowLayer(RenderLayerParent<The_Ultimate_TimeManagerEntity, The_Ultimate_Time_ManagerModel> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, The_Ultimate_TimeManagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(GLOW_TEXTURE));
            this.getParentModel().renderToBuffer(
                    pPoseStack,
                    vertexconsumer,
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F
            );
        }
    }
}