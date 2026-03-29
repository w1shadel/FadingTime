package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.tutm_entity.Time_HaloItemModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

@SuppressWarnings("removal")
public class TimeHaloCurioRenderer implements ICurioRenderer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TUTM.MODID, "textures/entity/time_halo.png");
    private static final int FULL_BRIGHT = 15728880;
    private final Time_HaloItemModel<LivingEntity> model;

    public TimeHaloCurioRenderer() {
        this.model = new Time_HaloItemModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(Time_HaloItemModel.LAYER_LOCATION));
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity entity = slotContext.entity();
        poseStack.pushPose();
        ICurioRenderer.translateIfSneaking(poseStack, entity);
        ICurioRenderer.rotateIfSneaking(poseStack, entity);
        poseStack.translate(0.0D, -0.7D, 0.0D);
        VertexConsumer vertexConsumer = renderTypeBuffer.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE));
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.renderToBuffer(
                poseStack,
                vertexConsumer,
                FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
        poseStack.popPose();
    }
}