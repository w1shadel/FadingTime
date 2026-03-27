package com.maxwell.tutm.client.renderer;

import com.maxwell.tutm.common.entity.ChronosGearEntity;
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

/**
 * クロノス・ギアのレンダラー
 * 歯車状の六角形リングを金色〜オレンジで描画し、待機中は回転のみ、
 * 飛翔中は螺旋残像エフェクト、バウンド中はパルスエフェクトを表示する
 */
public class ChronosGearRenderer extends EntityRenderer<ChronosGearEntity> {

    public ChronosGearRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChronosGearEntity entity, float entityYaw, float partialTick, PoseStack pose, MultiBufferSource buffer, int packedLight) {
        int state = entity.getGearState();
        float timer = entity.getStateTimer() + partialTick;

        pose.pushPose();

        // 1. 位置の補間 (レンダリング位置を滑らかに)
        // EntityRenderer#render の引数 entityYaw は、実は補間済みの Yaw です。
        // Pitchも補間して取得します。
        float interpolatedPitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        float interpolatedYaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());

        if (state == ChronosGearEntity.STATE_FLYING || state == ChronosGearEntity.STATE_BOUNCING) {
            // 進行方向を向く
            pose.mulPose(Axis.YP.rotationDegrees(interpolatedYaw));
            pose.mulPose(Axis.XP.rotationDegrees(interpolatedPitch));

            // 刃の向き（90度横倒し）
            pose.mulPose(Axis.YP.rotationDegrees(90.0f));

            // 自転 (高速回転)
            float spinSpeed = (state == ChronosGearEntity.STATE_BOUNCING) ? 60.0f : 40.0f;
            pose.mulPose(Axis.ZP.rotationDegrees(timer * spinSpeed));
        } else {
            // 待機中
            pose.mulPose(Axis.XP.rotationDegrees(90.0f));
            pose.mulPose(Axis.ZP.rotationDegrees(timer * 10.0f));
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentEmissive(getTextureLocation(entity)));
        Matrix4f mat = pose.last().pose();

        // 描画メソッドの呼び出し（PoseStackを渡すように変更済みのもの）
        switch (state) {
            case ChronosGearEntity.STATE_WAITING -> renderWaiting(pose, vc, mat, timer);
            case ChronosGearEntity.STATE_FLYING  -> renderFlying(pose, vc, mat, timer);
            case ChronosGearEntity.STATE_BOUNCING -> renderBouncing(pose, vc, mat, timer);
        }

        pose.popPose();
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);
    }

    /** 飛翔中: 非常に鋭利な多重刃 */
    private void renderFlying(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer) {
        float alpha = 0.9f;
        // メインの刃（厚みを少し薄くして鋭さを出す）
        draw3DGear(vc, mat, 1.0f, 0.15f, 20, 0.4f, 1.0f, 0.4f, 0.0f, alpha);

        // わずかに小さいサブの刃を逆回転させる（さらに凶悪に見える）
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * -80.0f)); // 超高速逆回転
        draw3DGear(vc, pose.last().pose(), 0.8f, 0.1f, 12, 0.2f, 1.0f, 0.2f, 0.0f, alpha * 0.7f);
        pose.popPose();
    }

    /** 待機中: 複数の重厚な金色のギアが互い違いに回転 */
    private void renderWaiting(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer) {
        float pulse = 0.6f + Mth.sin(timer * 0.15f) * 0.4f;

        // 1. 外側の巨大な親ギア（ゆっくり回転）
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * 2.0f)); // 追加の回転
        draw3DGear(vc, pose.last().pose(), 1.2f, 0.25f, 16, 0.2f, 0.9f, 0.7f, 0.1f, pulse);
        pose.popPose();

        // 2. 中間のギア（逆回転させて機械感を出す）
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * -4.0f)); // 逆回転
        pose.translate(0, 0, 0.05f); // 重ならないよう少し手前に
        draw3DGear(vc, pose.last().pose(), 0.7f, 0.2f, 10, 0.15f, 1.0f, 0.85f, 0.3f, pulse * 0.8f);
        pose.popPose();

        // 3. 中心コア（高速回転する小さな部品）
        pose.pushPose();
        pose.mulPose(Axis.ZP.rotationDegrees(timer * 10.0f));
        pose.translate(0, 0, 0.1f);
        draw3DGear(vc, pose.last().pose(), 0.25f, 0.3f, 6, 0.1f, 1.0f, 0.95f, 0.6f, pulse);
        pose.popPose();
    }


    /** バウンド中: 衝撃で膨張し、白熱する重厚なギア */
    private void renderBouncing(PoseStack pose, VertexConsumer vc, Matrix4f mat, float timer) {
        // 全体が伸び縮みする演出
        float scale = 1.0f + Mth.sin(timer * 0.4f) * 0.2f;
        float whiten = 0.5f + Mth.sin(timer * 0.4f) * 0.5f;

        pose.pushPose();
        pose.scale(scale, scale, scale);

        // バウンド時は「重さ」を出すために厚みを倍(0.5f)にする
        draw3DGear(vc, pose.last().pose(), 1.0f, 0.5f, 12, 0.2f, 1.0f, 0.7f + (whiten * 0.3f), whiten, 0.9f);

        // 内部のパーツが激しく振動
        pose.translate(Mth.sin(timer) * 0.05f, Mth.cos(timer) * 0.05f, 0);
        draw3DGear(vc, pose.last().pose(), 0.6f, 0.55f, 8, 0.15f, 0.9f, 0.8f, 0.4f, 0.7f);

        pose.popPose();
    }

    private void draw3DGear(VertexConsumer vc, Matrix4f mat,
                            float radius, float thickness, int toothCount, float toothDepth,
                            float r, float g, float b, float a) {

        float halfZ = thickness / 2.0f;
        float innerR = radius - toothDepth;
        float step = (float) (Math.PI * 2.0 / toothCount);

        for (int i = 0; i < toothCount; i++) {
            float angle = i * step;
            float nextAngle = (i + 1) * step;
            float midAngle = i * step + step * 0.5f;

            // 頂点座標の計算 (前面/背面 x 歯の外側/内側)
            // 歯の形状をハッキリさせるため、少し台形気味にする
            float x1_out = Mth.cos(angle + step * 0.2f) * radius;
            float y1_out = Mth.sin(angle + step * 0.2f) * radius;
            float x2_out = Mth.cos(angle + step * 0.8f) * radius;
            float y2_out = Mth.sin(angle + step * 0.8f) * radius;

            float x1_in = Mth.cos(angle) * innerR;
            float y1_in = Mth.sin(angle) * innerR;
            float x2_in = Mth.cos(nextAngle) * innerR;
            float y2_in = Mth.sin(nextAngle) * innerR;

            // 1. 前面の描画 (Z = +halfZ)
            drawQuad(vc, mat,
                    0, 0, halfZ,          // 中心
                    x1_in, y1_in, halfZ,
                    x2_in, y2_in, halfZ,
                    0, 0, halfZ, r, g, b, a, 0, 0, 1); // 法線は手前

            // 歯の突起部分（前面）
            drawQuad(vc, mat,
                    x1_in, y1_in, halfZ,
                    x1_out, y1_out, halfZ,
                    x2_out, y2_out, halfZ,
                    x2_in, y2_in, halfZ, r, g, b, a, 0, 0, 1);

            // 2. 背面の描画 (Z = -halfZ)
            drawQuad(vc, mat,
                    0, 0, -halfZ,
                    x2_in, y2_in, -halfZ,
                    x1_in, y1_in, -halfZ,
                    0, 0, -halfZ, r, g, b, a, 0, 0, -1); // 法線は奥

            // 歯の突起部分（背面）
            drawQuad(vc, mat,
                    x2_in, y2_in, -halfZ,
                    x2_out, y2_out, -halfZ,
                    x1_out, y1_out, -halfZ,
                    x1_in, y1_in, -halfZ, r, g, b, a, 0, 0, -1);

            // 3. 側面の描画 (厚み部分)
            // 歯の先端
            drawQuad(vc, mat,
                    x1_out, y1_out, halfZ,
                    x1_out, y1_out, -halfZ,
                    x2_out, y2_out, -halfZ,
                    x2_out, y2_out, halfZ, r * 0.8f, g * 0.8f, b * 0.8f, a, // 側面は少し暗く
                    Mth.cos(angle), Mth.sin(angle), 0);

            // 歯の谷間
            float x3_in = Mth.cos(nextAngle) * innerR;
            float y3_in = Mth.sin(nextAngle) * innerR;
            drawQuad(vc, mat,
                    x2_out, y2_out, halfZ,
                    x2_out, y2_out, -halfZ,
                    x2_in, y2_in, -halfZ,
                    x2_in, y2_in, halfZ, r * 0.7f, g * 0.7f, b * 0.7f, a,
                    Mth.cos(midAngle), Mth.sin(midAngle), 0);

            // 歯の横壁(立ち上がり)
            drawQuad(vc, mat,
                    x1_in, y1_in, halfZ,
                    x1_in, y1_in, -halfZ,
                    x1_out, y1_out, -halfZ,
                    x1_out, y1_out, halfZ, r * 0.9f, g * 0.9f, b * 0.9f, a,
                    -Mth.sin(angle), Mth.cos(angle), 0);
        }
    }

    private void drawQuad(VertexConsumer vc, Matrix4f mat,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          float r, float g, float b, float a,
                          float nx, float ny, float nz) {
        vc.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(0, 0).overlayCoords(0).uv2(240).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(1, 0).overlayCoords(0).uv2(240).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x3, y3, z3).color(r, g, b, a).uv(1, 1).overlayCoords(0).uv2(240).normal(nx, ny, nz).endVertex();
        vc.vertex(mat, x4, y4, z4).color(r, g, b, a).uv(0, 1).overlayCoords(0).uv2(240).normal(nx, ny, nz).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(ChronosGearEntity entity) {
        return new ResourceLocation("minecraft", "textures/misc/white.png");
    }
}
