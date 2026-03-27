package com.maxwell.tutm.client.tutm_entity;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class The_Ultimate_Time_ManagerModel extends HierarchicalModel<The_Ultimate_TimeManagerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TUTM.MODID, "the_ultimate_time_manager"), "main");
    public final ModelPart effectA;
    public final ModelPart effectB;
    private final ModelPart waist;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart halo;
    private final ModelPart short_1;
    private final ModelPart long_1;
    private final ModelPart behind_halo;
    private final ModelPart keyboard;
    private final ModelPart monitor2;
    private final ModelPart keyboard2;
    private final ModelPart monitor;
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
    private final ModelPart[] effectBShards;
    private final ModelPart effectB1;
    private final ModelPart effectB2;

    public The_Ultimate_Time_ManagerModel(ModelPart root) {
        this.waist = root.getChild("waist");
        this.body = this.waist.getChild("body");
        this.head = this.body.getChild("head");
        this.neck = this.head.getChild("neck");
        this.halo = this.head.getChild("halo");
        this.short_1 = this.head.getChild("short");
        this.long_1 = this.head.getChild("long");
        this.behind_halo = this.head.getChild("behind_halo");
        this.keyboard = this.body.getChild("keyboard");
        this.monitor2 = this.body.getChild("monitor2");
        this.keyboard2 = this.body.getChild("keyboard2");
        this.monitor = this.body.getChild("monitor");
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
        this.effectA = this.waist.getChild("effectA");
        this.effectB = this.waist.getChild("effectB");
        this.effectB1 = this.effectB.getChild("effectB1");
        this.effectB2 = this.effectB.getChild("effectB2");
        this.effectBShards = new ModelPart[48];
        for (int i = 0; i < 24; i++) {
            this.effectBShards[i] = this.effectB1.getChild("shard1_" + i);
            this.effectBShards[i + 24] = this.effectB2.getChild("shard2_" + i);
        }
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
        PartDefinition keyboard = body.addOrReplaceChild("keyboard", CubeListBuilder.create().texOffs(95, 117).addBox(-5.5F, -0.2F, -2.5F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, 15.2F, -6.5F));
        PartDefinition monitor2 = body.addOrReplaceChild("monitor2", CubeListBuilder.create().texOffs(73, 117).addBox(-5.5F, 0.0F, -5.5F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.5F, 6.8787F, 9.3075F));
        PartDefinition keyboard2 = body.addOrReplaceChild("keyboard2", CubeListBuilder.create().texOffs(95, 92).addBox(-2.9F, 0.0F, -3.5F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(6.9F, 19.0F, -5.5F));
        PartDefinition monitor = body.addOrReplaceChild("monitor", CubeListBuilder.create().texOffs(95, 105).addBox(-5.5F, 0.0F, -5.5F, 11.0F, 0.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(9.5F, 15.0F, -3.5F));
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
        PartDefinition effectA = waist.addOrReplaceChild("effectA", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        int segmentsA = 32;
        float radiusA = 120.0F;
        for (int i = 0; i < segmentsA; i++) {
            float angle = (float) (i * Math.PI * 2.0 / segmentsA);
            float x = (float) Math.cos(angle) * radiusA;
            float z = (float) Math.sin(angle) * radiusA;
            PartDefinition segment = effectA.addOrReplaceChild("ringA_" + i,
                    CubeListBuilder.create().texOffs(100, 100).addBox(-13.0F, -0.25F, -1.5F, 26.0F, 0.5F, 3.0F),
                    PartPose.offsetAndRotation(x, 0.0F, z, 0.0F, -angle + (float) Math.PI / 2F, 0.0F));
            segment.addOrReplaceChild("runeA_" + i,
                    CubeListBuilder.create().texOffs(100, 100).addBox(-0.5F, -3.0F, -1.7F, 1.0F, 6.0F, 0.2F),
                    PartPose.ZERO);
        }
        PartDefinition effectB = waist.addOrReplaceChild("effectB", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        float radiusB = 60.0F;
        int shardCountPerRing = 24;
        PartDefinition effectB1 = effectB.addOrReplaceChild("effectB1", CubeListBuilder.create(), PartPose.ZERO);
        for (int i = 0; i < shardCountPerRing; i++) {
            float angle = (float) (i * Math.PI * 2.0 / shardCountPerRing);
            float x = (float) Math.cos(angle) * radiusB;
            float z = (float) Math.sin(angle) * radiusB;
            float rx = (float) (Math.sin(i * 1.5) * Math.PI);
            float ry = (float) (Math.cos(i * 0.8) * Math.PI);
            float rz = (float) (Math.sin(i * 2.1) * Math.PI);
            CubeListBuilder builder = CubeListBuilder.create().texOffs(100, 100);
            if (i % 2 == 0) builder.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F);
            else builder.addBox(-0.5F, -2.0F, -0.5F, 1.0F, 4.0F, 1.0F);
            effectB1.addOrReplaceChild("shard1_" + i, builder, PartPose.offsetAndRotation(x, 0.0F, z, rx, ry, rz));
        }
        PartDefinition effectB2 = effectB.addOrReplaceChild("effectB2", CubeListBuilder.create(), PartPose.ZERO);
        for (int i = 0; i < shardCountPerRing; i++) {
            float angle = (float) (i * Math.PI * 2.0 / shardCountPerRing);
            float x = (float) Math.cos(angle) * radiusB;
            float z = (float) Math.sin(angle) * radiusB;
            float rx = (float) (Math.cos(i * 1.2) * Math.PI);
            float ry = (float) (Math.sin(i * 1.1) * Math.PI);
            float rz = (float) (Math.cos(i * 1.7) * Math.PI);
            CubeListBuilder builder = CubeListBuilder.create().texOffs(100, 100);
            if (i % 2 == 0) builder.addBox(-1.5F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F);
            else builder.addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F);
            effectB2.addOrReplaceChild("shard2_" + i, builder, PartPose.offsetAndRotation(x, 0.0F, z, rx, ry, rz));
        }
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(The_Ultimate_TimeManagerEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float time = pAgeInTicks;
        this.halo.zRot = time * 0.0314F;
        this.behind_halo.zRot = -time * 0.0157F;
        this.behind_halo.xRot = (float) Math.sin(time * 0.05F) * 0.1F;
        float longSpeed = 0.157F;
        this.long_1.zRot = (float) Math.PI + (time * longSpeed);
        float shortSpeed = longSpeed / 12.0F;
        this.short_1.zRot = 0.0F + (time * shortSpeed);
        this.animate(pEntity.idleAnimationState, The_Ultimate_Time_ManagerModelAnimation.IDLE, pAgeInTicks);
        this.animate(pEntity.walkAnimationState, The_Ultimate_Time_ManagerModelAnimation.WALK, pAgeInTicks);
        this.animate(pEntity.attackAlphaAnimationState, The_Ultimate_Time_ManagerModelAnimation.ATTACK_ALPHA, pAgeInTicks);
        float healthRatio = pEntity.getHealth() / pEntity.getMaxHealth();
        float damageFactor = 1.0F + (1.0F - healthRatio) * 2.0F;
        float baseTime = pAgeInTicks * damageFactor;
        this.effectA.yRot = baseTime * 0.02F;
        if (pEntity.isSecondForm()) {
            this.effectB.visible = true;
            this.effectB1.yRot = -baseTime * 0.05F;
            this.effectB1.xRot = (float) Math.sin(baseTime * 0.02F) * 0.5F;
            this.effectB2.yRot = baseTime * 0.08F;
            this.effectB2.zRot = (float) Math.cos(baseTime * 0.03F) * 0.6F;
            for (int i = 0; i < 48; i++) {
                ModelPart shard = this.effectBShards[i];
                float shardTime = baseTime + (i * 0.5F);
                shard.xRot = shardTime * 0.1F + (float) Math.sin(shardTime * 0.15F) * 0.5F;
                shard.yRot = shardTime * 0.08F + (float) Math.cos(shardTime * 0.12F) * 0.5F;
                shard.zRot = shardTime * 0.09F;
            }
        } else {
            this.effectB.visible = false;
        }
    }

    @Override
    public ModelPart root() {
        return waist;
    }
}