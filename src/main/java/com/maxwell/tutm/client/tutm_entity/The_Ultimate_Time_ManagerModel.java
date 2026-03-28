package com.maxwell.tutm.client.tutm_entity;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class The_Ultimate_Time_ManagerModel extends HierarchicalModel<The_Ultimate_TimeManagerEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(TUTM.MODID, "the_ultimate_time_manager_model"), "main");
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
    private final ModelPart rightArm;
    private final ModelPart under;
    private final ModelPart rightItem;
    private final ModelPart leftArm;
    private final ModelPart under2;
    private final ModelPart leftItem;
    private final ModelPart rightLeg;
    private final ModelPart under3;
    private final ModelPart skurt;
    private final ModelPart leftLeg;
    private final ModelPart under4;
    private final ModelPart skurt2;
    private final ModelPart[] effectBShards;
    private final ModelPart effectB1;
    private final ModelPart effectB2;

    public The_Ultimate_Time_ManagerModel(ModelPart root) {
        this.waist = root.getChild("waist");
        this.body = this.waist.getChild("body");
        this.head = this.body.getChild("head");
        this.neck = this.head.getChild("neck");
        this.halo = this.head.getChild("halo");
        this.short_1 = this.head.getChild("short_1");
        this.long_1 = this.head.getChild("long_1");
        this.behind_halo = this.head.getChild("behind_halo");
        this.rightArm = this.body.getChild("rightArm");
        this.under = this.rightArm.getChild("under");
        this.rightItem = this.rightArm.getChild("rightItem");
        this.leftArm = this.body.getChild("leftArm");
        this.under2 = this.leftArm.getChild("under2");
        this.leftItem = this.leftArm.getChild("leftItem");
        this.rightLeg = this.body.getChild("rightLeg");
        this.under3 = this.rightLeg.getChild("under3");
        this.skurt = this.rightLeg.getChild("skurt");
        this.leftLeg = this.body.getChild("leftLeg");
        this.under4 = this.leftLeg.getChild("under4");
        this.skurt2 = this.leftLeg.getChild("skurt2");
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
        PartDefinition body = waist.addOrReplaceChild("body", CubeListBuilder.create().texOffs(78, 56).addBox(1.6282F, 7.0333F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 56).addBox(-2.5718F, 7.0333F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(87, 30).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(77, 63).addBox(3.0F, 2.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(44, 48).addBox(-3.0F, 2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(88, 63).addBox(-4.0F, 2.0F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, 0.0F));
        PartDefinition body_r1 = body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(51, 65).addBox(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.8F, -0.7854F, 0.0F, 0.0F));
        PartDefinition body_r2 = body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(89, 50).addBox(-0.5F, -0.7F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.8F, 7.2F, -0.5F, 0.0F, 0.0F, -0.3491F));
        PartDefinition body_r3 = body.addOrReplaceChild("body_r3", CubeListBuilder.create().texOffs(78, 50).addBox(-1.5F, -1.0F, -2.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8F, 7.5F, 0.5F, 0.0F, 0.0F, 0.3491F));
        PartDefinition body_r4 = body.addOrReplaceChild("body_r4", CubeListBuilder.create().texOffs(89, 56).addBox(-1.6F, -0.95F, -2.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, 9.45F, 0.5F, 0.0F, 0.0F, 0.2618F));
        PartDefinition body_r5 = body.addOrReplaceChild("body_r5", CubeListBuilder.create().texOffs(78, 56).addBox(-1.5F, -0.75F, -2.0F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7F, 8.75F, 0.5F, 0.0F, 0.0F, -0.2618F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition neck = head.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(44, 32).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(82, 0).addBox(-3.8981F, -6.5564F, -3.9F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head_r1 = neck.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(109, 24).addBox(-1.0F, -4.0F, 0.0F, 3.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.4F, -2.2F, 4.1F, 0.0F, 0.0F, 0.2182F));
        PartDefinition head_r2 = neck.addOrReplaceChild("head_r2", CubeListBuilder.create().texOffs(109, 24).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5F, -2.2F, 4.2F, 0.0F, 0.0F, -0.1309F));
        PartDefinition head_r3 = neck.addOrReplaceChild("head_r3", CubeListBuilder.create().texOffs(114, 14).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 9.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.2F, -2.2F, 4.2F, 0.0F, 0.0F, 0.2182F));
        PartDefinition head_r4 = neck.addOrReplaceChild("head_r4", CubeListBuilder.create().texOffs(109, 9).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -2.2F, 4.2F, 0.0F, 0.0F, 0.0436F));
        PartDefinition head_r5 = neck.addOrReplaceChild("head_r5", CubeListBuilder.create().texOffs(87, 13).addBox(-2.5F, -4.0F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.5924F, -2.1914F, 2.5043F, 2.7286F, -1.5503F, -2.4823F));
        PartDefinition head_r6 = neck.addOrReplaceChild("head_r6", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2924F, -2.1914F, -0.5957F, 2.6588F, -1.5503F, -2.4823F));
        PartDefinition head_r7 = neck.addOrReplaceChild("head_r7", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1924F, -2.1914F, -3.2957F, -1.6581F, -1.3614F, 1.8326F));
        PartDefinition head_r8 = neck.addOrReplaceChild("head_r8", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3924F, -2.1914F, 2.6043F, 0.0F, -1.5708F, 0.2618F));
        PartDefinition head_r9 = neck.addOrReplaceChild("head_r9", CubeListBuilder.create().texOffs(88, 13).addBox(-1.0F, -4.0F, -1.0F, 3.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7F, -2.2F, 1.8F, 1.5708F, 1.4399F, 1.4835F));
        PartDefinition head_r10 = neck.addOrReplaceChild("head_r10", CubeListBuilder.create().texOffs(113, 35).addBox(-1.5F, -4.0F, 0.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.1019F, 3.1436F, -4.1F, -2.9671F, 0.0F, 3.1416F));
        PartDefinition head_r11 = neck.addOrReplaceChild("head_r11", CubeListBuilder.create().texOffs(113, 35).addBox(-1.5F, -4.0F, 0.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1981F, 5.2436F, -5.1F, -2.9671F, 0.0F, 3.1416F));
        PartDefinition head_r12 = neck.addOrReplaceChild("head_r12", CubeListBuilder.create().texOffs(113, 35).addBox(-1.5F, -4.0F, 0.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1981F, 3.8436F, -5.1F, -2.9671F, 0.0F, 3.1416F));
        PartDefinition head_r13 = neck.addOrReplaceChild("head_r13", CubeListBuilder.create().texOffs(113, 35).addBox(-1.5F, -4.0F, 0.5F, 2.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.9981F, 0.4436F, -4.8F, -2.9671F, 0.0F, 3.1416F));
        PartDefinition head_r14 = neck.addOrReplaceChild("head_r14", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.1981F, -2.1564F, -4.4F, -2.9671F, 0.0F, 3.1416F));
        PartDefinition head_r15 = neck.addOrReplaceChild("head_r15", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 10.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1981F, -2.1564F, -4.4F, -3.0718F, 0.0F, 3.1416F));
        PartDefinition head_r16 = neck.addOrReplaceChild("head_r16", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.8019F, -2.1564F, -4.4F, -3.0718F, 0.0F, 3.1416F));
        PartDefinition head_r17 = neck.addOrReplaceChild("head_r17", CubeListBuilder.create().texOffs(88, 13).addBox(-1.5F, -4.0F, -0.5F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.2019F, -2.1564F, -2.8F, -1.4748F, 1.4399F, -1.6581F));
        PartDefinition head_r18 = neck.addOrReplaceChild("head_r18", CubeListBuilder.create().texOffs(88, 13).addBox(-1.0F, -4.0F, -1.0F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -2.2F, 0.2F, 0.0F, 1.5708F, -0.0873F));
        PartDefinition head_r19 = neck.addOrReplaceChild("head_r19", CubeListBuilder.create().texOffs(88, 13).addBox(-1.0F, -4.0F, -1.0F, 3.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.5F, -2.2F, 3.2F, 0.0F, 1.5708F, -0.0873F));
        PartDefinition head_r20 = neck.addOrReplaceChild("head_r20", CubeListBuilder.create().texOffs(107, 14).addBox(-1.0F, -4.0F, 0.0F, 2.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -2.2F, 4.2F, 0.0F, 0.0F, -0.0873F));
        PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create().texOffs(0, 32).addBox(-11.0F, -11.0F, 0.0F, 22.0F, 22.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -9.0F));
        PartDefinition short_1 = head.addOrReplaceChild("short_1", CubeListBuilder.create().texOffs(32, 54).addBox(-0.5F, -7.0F, 0.0F, 1.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -8.8F));
        PartDefinition long_1 = head.addOrReplaceChild("long_1", CubeListBuilder.create().texOffs(48, 64).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 11.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -8.8F));
        PartDefinition behind_halo = head.addOrReplaceChild("behind_halo", CubeListBuilder.create().texOffs(0, 0).addBox(-16.0F, -16.0F, 0.0F, 32.0F, 32.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -11.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(34, 65).addBox(-2.0F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition under = rightArm.addOrReplaceChild("under", CubeListBuilder.create().texOffs(66, 1).addBox(-2.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));
        PartDefinition rightItem = rightArm.addOrReplaceChild("rightItem", CubeListBuilder.create(), PartPose.offset(-1.2432F, 7.4578F, 1.4792F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(65, 21).addBox(-2.0F, 2.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        PartDefinition under2 = leftArm.addOrReplaceChild("under2", CubeListBuilder.create().texOffs(65, 11).addBox(-2.0F, 1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition leftItem = leftArm.addOrReplaceChild("leftItem", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 1.0F));
        PartDefinition rightLeg = body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 70).addBox(-1.6F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition under3 = rightLeg.addOrReplaceChild("under3", CubeListBuilder.create().texOffs(0, 80).addBox(-1.6F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition skurt = rightLeg.addOrReplaceChild("skurt", CubeListBuilder.create(), PartPose.offset(0.0F, -1.1F, 0.0F));
        PartDefinition cube_r1 = skurt.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(107, 108).addBox(-0.5F, -8.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 7.4F, 3.2F, 1.5708F, -1.3526F, -1.5708F));
        PartDefinition cube_r2 = skurt.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(97, 119).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 9.6852F, -3.7529F, -1.5708F, 1.4312F, -1.5708F));
        PartDefinition cube_r3 = skurt.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(118, 116).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 6.4F, -3.1F, -1.5708F, 1.309F, -1.5708F));
        PartDefinition cube_r4 = skurt.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(106, 98).addBox(-0.5F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.1F, 5.2185F, 2.5178F, 1.5708F, -1.4573F, -1.5708F));
        PartDefinition cube_r5 = skurt.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(118, 103).addBox(-0.5F, -4.0F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.6319F, 9.7909F, 0.0F, 0.0F, 0.0F, 0.1484F));
        PartDefinition cube_r6 = skurt.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(108, 118).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2F, 6.4F, 0.0F, 0.0F, 0.0F, 0.2705F));
        PartDefinition leftLeg = body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(12, 70).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition under4 = leftLeg.addOrReplaceChild("under4", CubeListBuilder.create().texOffs(12, 80).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 6.0F, 0.0F));
        PartDefinition skurt2 = leftLeg.addOrReplaceChild("skurt2", CubeListBuilder.create(), PartPose.offset(0.2F, 5.4F, -3.2F));
        PartDefinition cube_r7 = skurt2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(11, 109).addBox(-0.5F, -2.0F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 3.1852F, -0.5529F, -1.5708F, 1.4312F, -1.5708F));
        PartDefinition cube_r8 = skurt2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(2, 121).addBox(-0.5F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, -1.2815F, 5.8178F, 1.5708F, -1.4573F, -1.5708F));
        PartDefinition cube_r9 = skurt2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(13, 119).addBox(-0.5F, -8.5F, -2.0F, 1.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.1F, 0.9F, 6.4F, 1.5708F, -1.3526F, -1.5708F));
        PartDefinition cube_r10 = skurt2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 109).addBox(-0.5F, -4.0F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5319F, 3.2909F, 3.2F, 0.0F, 0.0F, -0.1484F));
        PartDefinition cube_r11 = skurt2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 99).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.1F, -0.1F, 3.2F, 0.0F, 0.0F, -0.2705F));
        PartDefinition cube_r12 = skurt2.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(25, 116).addBox(-0.5F, -6.5F, -2.0F, 1.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.1F, -1.5708F, 1.309F, -1.5708F));
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
        float yawRad = pNetHeadYaw * ((float) Math.PI / 180F);
        float pitchRad = pHeadPitch * ((float) Math.PI / 180F);
        this.head.yRot = yawRad;
        this.head.xRot = pitchRad;
        float time = pAgeInTicks;
        this.halo.zRot = time * 0.0314F;
        this.behind_halo.zRot = -time * 0.0157F;
        this.behind_halo.xRot = (float) Math.sin(time * 0.05F) * 0.1F;
        float longSpeed = 0.157F;
        this.long_1.zRot = (float) Math.PI + (time * longSpeed);
        float shortSpeed = longSpeed / 12.0F;
        this.short_1.zRot = 0.0F + (time * shortSpeed);
        this.animate(pEntity.idleAnimationState, The_Ultimate_Time_ManagerModelAnimation.IDLE, pAgeInTicks);
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

    @Override
    public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
        super.renderToBuffer(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
    }
}