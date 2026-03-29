package com.maxwell.tutm.client.tutm_entity;

import com.maxwell.tutm.TUTM;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("removal")
public class Time_HaloItemModel<T extends LivingEntity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TUTM.MODID, "time_haloitem"), "main");
    private final ModelPart root;
    private final ModelPart halo;
    private final ModelPart short_1;
    private final ModelPart long_1;
    private final ModelPart behind_halo;

    public Time_HaloItemModel(ModelPart root) {
        this.root = root.getChild("root");
        this.halo = this.root.getChild("halo");
        this.short_1 = this.root.getChild("short_1");
        this.long_1 = this.root.getChild("long_1");
        this.behind_halo = this.root.getChild("behind_halo");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 6.0F, 8.6F));
        PartDefinition halo = root.addOrReplaceChild("halo", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -1.4F));
        PartDefinition cube_r1 = halo.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-11.0F, -12.0F, -0.1F, 22.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -0.1F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition short_1 = root.addOrReplaceChild("short_1", CubeListBuilder.create(), PartPose.offset(0.0F, -0.3F, -1.6F));
        PartDefinition cube_r2 = short_1.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(44, 43).addBox(-0.5F, -8.0F, 0.1F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.3F, 0.1F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition long_1 = root.addOrReplaceChild("long_1", CubeListBuilder.create(), PartPose.offset(0.0F, 0.1F, -1.6F));
        PartDefinition cube_r3 = long_1.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(44, 32).addBox(-0.5F, -1.0F, 0.1F, 1.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.9F, 0.1F, -3.1416F, 0.0F, 3.1416F));
        PartDefinition behind_halo = root.addOrReplaceChild("behind_halo", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.4F));
        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float healthRatio = entity.getHealth() / entity.getMaxHealth();
        float damageFactor = 1.0F + (1.0F - healthRatio) * 2.0F;
        float baseTime = ageInTicks * damageFactor;
        this.halo.zRot = baseTime * 0.0314F;
        this.behind_halo.zRot = -baseTime * 0.0157F;
        this.behind_halo.xRot = (float) Math.sin(baseTime * 0.05F) * 0.1F;
        float longSpeed = 0.157F;
        this.long_1.zRot = (float) Math.PI + (baseTime * longSpeed);
        float shortSpeed = longSpeed / 12.0F;
        this.short_1.zRot = 0.0F + (baseTime * shortSpeed);
    }
}