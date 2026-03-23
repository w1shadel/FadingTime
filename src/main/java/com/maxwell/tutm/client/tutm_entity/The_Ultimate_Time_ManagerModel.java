package com.maxwell.tutm.client.tutm_entity;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

public class The_Ultimate_Time_ManagerModel extends HierarchicalModel<The_Ultimate_TimeManagerEntity> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TUTM.MODID, "tutm"), "main");
	private final ModelPart waist;
	private final ModelPart body;
	private final ModelPart head;
	private final ModelPart neck;
	private final ModelPart halo;
	private final ModelPart short_1;
	private final ModelPart long_1;
	private final ModelPart behind_halo;
	private final ModelPart rightArm;
	private final ModelPart under;
	private final ModelPart rightItem;
	private final ModelPart leftArm;
	private final ModelPart under2;
	private final ModelPart leftItem;
	private final ModelPart rightLeg;
	private final ModelPart under3;
	private final ModelPart leftLeg;
	private final ModelPart under4;

	public The_Ultimate_Time_ManagerModel(ModelPart root) {
		this.waist = root.getChild("waist");
		this.body = this.waist.getChild("body");
		this.head = this.body.getChild("head");
		this.neck = this.head.getChild("neck");
		this.halo = this.head.getChild("halo");
		this.short_1 = this.head.getChild("short");
		this.long_1 = this.head.getChild("long");
		this.behind_halo = this.head.getChild("behind_halo");
		this.rightArm = this.body.getChild("rightArm");
		this.under = this.rightArm.getChild("under");
		this.rightItem = this.rightArm.getChild("rightItem");
		this.leftArm = this.body.getChild("leftArm");
		this.under2 = this.leftArm.getChild("under2");
		this.leftItem = this.leftArm.getChild("leftItem");
		this.rightLeg = this.body.getChild("rightLeg");
		this.under3 = this.rightLeg.getChild("under3");
		this.leftLeg = this.body.getChild("leftLeg");
		this.under4 = this.leftLeg.getChild("under4");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition waist = partdefinition.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(44, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));

		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition neck = head.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(44, 32).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

		PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(0, 32).addBox(-11.0F, -11.0F, 0.0F, 22.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -9.0F));

		PartDefinition short_1 = head.addOrReplaceChild("short", CubeListBuilder.create().texOffs(32, 54).addBox(-0.5F, -7.0F, 0.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -8.8F));

		PartDefinition long_1 = head.addOrReplaceChild("long", CubeListBuilder.create().texOffs(48, 64).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -8.8F));

		PartDefinition behind_halo = head.addOrReplaceChild("behind_halo", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -11.0F));

		PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(32, 64).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.0F, 0.0F));

		PartDefinition under = rightArm.addOrReplaceChild("under", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition rightItem = rightArm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 1.0F));

		PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(64, 20).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));

		PartDefinition under2 = leftArm.addOrReplaceChild("under2", CubeListBuilder.create().texOffs(64, 10).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition leftItem = leftArm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 1.0F));

		PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));

		PartDefinition under3 = rightLeg.addOrReplaceChild("under3", CubeListBuilder.create().texOffs(0, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(16, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));

		PartDefinition under4 = leftLeg.addOrReplaceChild("under4", CubeListBuilder.create().texOffs(16, 54).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

    @Override
    public ModelPart root() {
        return waist;
    }

    @Override
    public void setupAnim(The_Ultimate_TimeManagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float time = pAgeInTicks;
        this.halo.zRot = time * 0.0314F;
        this.behind_halo.zRot = -time * 0.0157F;
        this.behind_halo.xRot = (float) Math.sin(time * 0.05F) * 0.1F;
        float longSpeed = 0.157F;
        this.long_1.zRot = (float)Math.PI + (time * longSpeed);
        float shortSpeed = longSpeed / 12.0F;
        this.short_1.zRot = 0.0F + (time * shortSpeed);
        this.animate(pEntity.idleAnimationState, The_Ultimate_Time_ManagerModelAnimation.IDLE, pAgeInTicks);
        this.animate(pEntity.walkAnimationState, The_Ultimate_Time_ManagerModelAnimation.WALK, pAgeInTicks);
        this.animate(pEntity.attackAlphaAnimationState, The_Ultimate_Time_ManagerModelAnimation.ATTACK_ALPHA, pAgeInTicks);
    }
}