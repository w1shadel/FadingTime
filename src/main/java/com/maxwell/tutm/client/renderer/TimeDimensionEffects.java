package com.maxwell.tutm.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class TimeDimensionEffects extends DimensionSpecialEffects {
    public TimeDimensionEffects() {
        super(Float.NaN, true, SkyType.NONE, false, false);
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 biomeFogColor, float daylight) {
        // 神聖さを際立たせる、少し白みを帯びた黄金と紫かすむ霧
        return new Vec3(0.3, 0.2, 0.35);
    }

    @Override
    public boolean isFoggyAt(int x, int y) {
        return false;
    }

    @Override
    public boolean renderSky(ClientLevel level, int ticks, float partialTick, PoseStack poseStack, Camera camera, Matrix4f projectionMatrix, boolean isFoggy, Runnable setupFog) {
        float time = (float) level.getGameTime() + partialTick;

        // 背景のベース色 (神聖な深紫)
        RenderSystem.clearColor(0.08F, 0.05F, 0.15F, 1.0F);
        GlStateManager._clear(16384, Minecraft.ON_OSX);

        RenderSystem.depthMask(false);
        setupFog.run();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();

        // 1. グラデーションの空 (神秘的なエメラルド・ブルーベース)
        renderGradientSky(poseStack, builder, time);

        // 2. 動的な星空
        renderStars(poseStack, builder, time);

        // 3. プロシージャルなオーロラエフェクト (画像を使わない)
        renderAurora(poseStack, builder, time);

        // 4. 時を司るクリスタル (画像を使わないジオメトリ)
        renderTimeCrystals(level, partialTick, poseStack);

        RenderSystem.depthMask(true);
        return true;
    }

    private void renderGradientSky(PoseStack poseStack, BufferBuilder builder, float time) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();
        float size = 100.0f;

        // 天頂色 (神聖感のある宇宙の深紫・インディゴ)
        int topR = 25, topG = 15, topB = 60;
        // 地平線色 (まばゆい黄金の夜明け)
        int botR = 255, botG = 200, botB = 100;
        
        // 神々しく降り注ぐ光の脈動
        float pulse = (Mth.sin(time * 0.015f) + 1.0f) * 0.5f;
        
        // 輝きがより強くなるように
        botR = Math.min(255, botR + (int)(pulse * 30));
        botG = Math.min(255, botG + (int)(pulse * 40));
        botB = Math.min(255, botB + (int)(pulse * 50));

        // 地平線の色を上空に伸ばすための第二グラデーション（少し中間色を意識）
        // 天頂部分も少し明るくする
        topR += (int)(pulse * 5);
        topG += (int)(pulse * 5);
        topB += (int)(pulse * 10);

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // 上面 (天頂)
        builder.vertex(matrix, -size, size, -size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, -size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, size, -size).color(topR, topG, topB, 255).endVertex();

        // 側面 (上下のグラデーション) -> 北
        builder.vertex(matrix, -size, size, -size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, size, -size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, -size, -size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, -size, -size, -size).color(botR, botG, botB, 255).endVertex();
        // 南
        builder.vertex(matrix, size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, -size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, -size, -size, size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, size, -size, size).color(botR, botG, botB, 255).endVertex();
        // 東
        builder.vertex(matrix, size, size, -size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, size, -size, size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, size, -size, -size).color(botR, botG, botB, 255).endVertex();
        // 西
        builder.vertex(matrix, -size, size, size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, -size, size, -size).color(topR, topG, topB, 255).endVertex();
        builder.vertex(matrix, -size, -size, -size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, -size, -size, size).color(botR, botG, botB, 255).endVertex();
        
        // 底面
        builder.vertex(matrix, -size, -size, size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, -size, -size, -size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, size, -size, -size).color(botR, botG, botB, 255).endVertex();
        builder.vertex(matrix, size, -size, size).color(botR, botG, botB, 255).endVertex();

        Tesselator.getInstance().end();
        poseStack.popPose();
    }

    private void renderStars(PoseStack poseStack, BufferBuilder builder, float time) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        RandomSource random = RandomSource.create(10842L); // シード固定
        poseStack.pushPose();
        
        // 空全体をゆっくりと回転させる
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 0.05f));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(time * 0.02f));
        Matrix4f matrix = poseStack.last().pose();

        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        
        // 1500個の星を描画
        for (int i = 0; i < 1500; ++i) {
            double x = (double)(random.nextFloat() * 2.0F - 1.0F);
            double y = (double)(random.nextFloat() * 2.0F - 1.0F);
            double z = (double)(random.nextFloat() * 2.0F - 1.0F);
            double size = (double)(0.15F + random.nextFloat() * 0.1F);
            double length = x * x + y * y + z * z;
            
            if (length < 1.0D && length > 0.01D) {
                length = 1.0D / Math.sqrt(length);
                x *= length;
                y *= length;
                z *= length;
                double px = x * 100.0D;
                double py = y * 100.0D;
                double pz = z * 100.0D;
                
                double angleXZ = Math.atan2(x, z);
                double s = Math.sin(angleXZ);
                double c = Math.cos(angleXZ);
                double angleY = Math.atan2(Math.sqrt(x * x + z * z), y);
                double sy = Math.sin(angleY);
                double cy = Math.cos(angleY);
                double randomRot = random.nextDouble() * Math.PI * 2.0D;
                double sRot = Math.sin(randomRot);
                double cRot = Math.cos(randomRot);
                
                // それぞれの星ごとのまたたきのフェーズ
                float twinkle = random.nextFloat();
                int alpha = (int)(255 * (0.3f + 0.7f * Mth.sin(time * 0.1f * twinkle + (float)randomRot * 10.0f)));
                if (alpha < 0) alpha = 0;
                if (alpha > 255) alpha = 255;
                
                // 星の色を決める (神聖な白、シャンパンゴールド、淡いシアン)
                int r = 255, g = 255, b = 255;
                float colorChance = random.nextFloat();
                if (colorChance < 0.2f) { // シャンパンゴールド（神聖）
                    r = 255; g = 235; b = 150;
                } else if (colorChance < 0.35f) { // 淡いシアン・ブルー
                    r = 200; g = 230; b = 255;
                }
                
                // 一部の星を極端に明るく大きく（神なる星）
                if (random.nextFloat() < 0.02f) {
                    alpha = 255; // 常に明るい
                    size *= 4.0; // サイズ倍増
                }

                for (int j = 0; j < 4; ++j) {
                    double vx = (double)((j & 2) - 1) * size;
                    double vy = (double)((j + 1 & 2) - 1) * size;
                    double vz = vx * cRot - vy * sRot;
                    double vw = vy * cRot + vx * sRot;
                    double px2 = vz * sy + 0.0D * cy;
                    double py2 = 0.0D * sy - vz * cy;
                    double finalX = px2 * s - vw * c;
                    double finalY = py2;
                    double finalZ = vw * s + px2 * c;
                    builder.vertex(matrix, (float)(px + finalX), (float)(py + finalY), (float)(pz + finalZ)).color(r, g, b, alpha).endVertex();
                }
            }
        }
        Tesselator.getInstance().end();
        poseStack.popPose();
    }

    private void renderAurora(PoseStack poseStack, BufferBuilder builder, float time) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        // 光の帯（エンジェリック・リボン）を表現するための加算合成
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.disableCull();

        poseStack.pushPose();
        Matrix4f matrix = poseStack.last().pose();

        int numRibbons = 5; // 光の層
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (int i = 0; i < numRibbons; i++) {
            // かなり巨大に配置して大空を覆う
            float zStart = -500.0f;
            float zEnd = 500.0f;
            float segments = 100;
            float step = (zEnd - zStart) / segments;

            // 光の帯の高さ、中間帯の高さ (天から降り注ぐように高さを調整)
            float yBase = 90.0f + i * 25.0f;
            float yMidOffset = 60.0f + Mth.sin(time * 0.01f + i) * 15.0f;
            float curtainHeight = 180.0f + Mth.sin(time * 0.015f + i) * 30.0f;
            
            // 光の色 (神聖な白金、ペールゴールド、プラチナブルー)
            int rCol = i % 2 == 0 ? 255 : 200;
            int gCol = 240 - i * 5;
            int bCol = 210 + i * 10;

            // 天空を滑らかに漂う軌道
            float phase = time * 0.003f + i * 4.2f;

            for (float z = zStart; z < zEnd; z += step) {
                // 非常になだらかで雄大なカーブ (光の波紋)
                float wave1 = (float) Math.sin(z * 0.002f + phase) * 120.0f;
                float wave2 = (float) Math.cos(z * 0.004f - phase * 1.2f) * 60.0f;
                float x1 = wave1 + wave2;

                float wave1_next = (float) Math.sin((z + step) * 0.002f + phase) * 120.0f;
                float wave2_next = (float) Math.cos((z + step) * 0.004f - phase * 1.2f) * 60.0f;
                float x2 = wave1_next + wave2_next;
                
                // Y軸方向の滑らかで大きな揺らぎ
                float y1_base = yBase + (float) Math.sin(z * 0.004f + time * 0.01f) * 20.0f;
                float y2_base = yBase + (float) Math.sin((z + step) * 0.004f + time * 0.01f) * 20.0f;

                // 遠景（端）に行くほどなめらかにフェードアウト
                float dist1 = Math.min(Math.abs(z) / 400.0f, 1.0f);
                // 神聖な光なので透明でもやや明るさを残す
                float alphaMultiplier1 = (1.0f - dist1 * dist1) * 0.5f; 
                
                float dist2 = Math.min(Math.abs(z + step) / 400.0f, 1.0f);
                float alphaMultiplier2 = (1.0f - dist2 * dist2) * 0.5f;
                
                int aMid1 = (int)(255 * alphaMultiplier1);
                int aMid2 = (int)(255 * alphaMultiplier2);
                int aZero = 0; // 上端と下端を完全に透明にして「紐」の境界線を消す

                // --- 下半分の幕 (透明な根本 -> 中央帯で発光) ---
                builder.vertex(matrix, x1, y1_base, z).color(rCol, gCol, bCol, aZero).endVertex();
                builder.vertex(matrix, x2, y2_base, z + step).color(rCol, gCol, bCol, aZero).endVertex();
                builder.vertex(matrix, x2, y2_base + yMidOffset, z + step).color(rCol, gCol, bCol, aMid2).endVertex();
                builder.vertex(matrix, x1, y1_base + yMidOffset, z).color(rCol, gCol, bCol, aMid1).endVertex();

                // --- 上半分の幕 (中央帯 -> 上空へ向かって消えていく) ---
                builder.vertex(matrix, x1, y1_base + yMidOffset, z).color(rCol, gCol, bCol, aMid1).endVertex();
                builder.vertex(matrix, x2, y2_base + yMidOffset, z + step).color(rCol, gCol, bCol, aMid2).endVertex();
                builder.vertex(matrix, x2, y2_base + curtainHeight, z + step).color(rCol, gCol, bCol, aZero).endVertex();
                builder.vertex(matrix, x1, y1_base + curtainHeight, z).color(rCol, gCol, bCol, aZero).endVertex();
            }
        }
        Tesselator.getInstance().end();
        RenderSystem.enableCull();
        poseStack.popPose();
    }

    private void renderTimeCrystals(ClientLevel level, float partialTick, PoseStack poseStack) {
        float time = (float) level.getGameTime() + partialTick;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        RenderSystem.disableCull();

        // テクスチャを使わず、プロシージャルなクリスタルシェイプを描画
        drawCrystal(poseStack, time, 40, 45, 40, 4.0f, 0);
        drawCrystal(poseStack, time, -50, 30, -30, 2.5f, 1);
        drawCrystal(poseStack, time, 25, 70, -60, 6.0f, 2);
        drawCrystal(poseStack, time, -70, 50, 80, 5.0f, 3);
        drawCrystal(poseStack, time, 0, 90, 0, 8.0f, 4);   
        drawCrystal(poseStack, time, -30, 60, 20, 3.0f, 5); 

        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawCrystal(PoseStack poseStack, float time, float x, float y, float z, float scale, int index) {
        poseStack.pushPose();
        
        float bobbing = Mth.sin(time * 0.03f + index) * 5.0f;
        poseStack.translate(x, y + bobbing, z);
        
        // 独自回転
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(time * 1.5f + index * 45));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(Mth.sin(time * 0.02f + index) * 15f));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(Mth.cos(time * 0.02f + index) * 10f));
        
        poseStack.scale(scale, scale, scale);
        Matrix4f matrix = poseStack.last().pose();
        
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.getBuilder();
        
        // 輝くダイヤモンド・純白ゴールドの輝き (神聖)
        float glow = (Mth.sin(time * 0.05f + index) + 1.0f) * 0.5f;
        int r = 255;
        int g = (int)(245 + 10 * glow);
        int b = (int)(220 + 35 * glow);
        int alpha = (int)(180 + 75 * glow); 

        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        
        // 上半分のピラミッド (頂点は 0, 2, 0)
        addTriangle(builder, matrix, 0, 2, 0,  1, 0, 0,  0, 0, 1, r, g, b, alpha);
        addTriangle(builder, matrix, 0, 2, 0,  0, 0, 1, -1, 0, 0, r, g, b, alpha);
        addTriangle(builder, matrix, 0, 2, 0, -1, 0, 0,  0, 0,-1, r, g, b, alpha);
        addTriangle(builder, matrix, 0, 2, 0,  0, 0,-1,  1, 0, 0, r, g, b, alpha);
        
        // 下半分のピラミッド (頂点は 0, -2, 0)
        int rB = r / 2; int gB = g / 2; int bB = b / 2;
        addTriangle(builder, matrix, 0, -2, 0,  0, 0, 1,  1, 0, 0, rB, gB, bB, alpha);
        addTriangle(builder, matrix, 0, -2, 0, -1, 0, 0,  0, 0, 1, rB, gB, bB, alpha);
        addTriangle(builder, matrix, 0, -2, 0,  0, 0,-1, -1, 0, 0, rB, gB, bB, alpha);
        addTriangle(builder, matrix, 0, -2, 0,  1, 0, 0,  0, 0,-1, rB, gB, bB, alpha);

        Tesselator.getInstance().end();
        poseStack.popPose();
    }
    
    private void addTriangle(BufferBuilder builder, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, int r, int g, int b, int a) {
        builder.vertex(matrix, x1, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        builder.vertex(matrix, x3, y3, z3).color(r, g, b, a).endVertex();
    }
}