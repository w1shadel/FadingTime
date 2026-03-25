package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.EntityState;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelTimeManagerMixin {
    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void tutm$manageTemporalFlow(Consumer<T> pConsumerEntity, T pEntity, CallbackInfo ci) {
        // --- 1. 逆行（Rewind）処理 ---
        if (TimeManager.isRewinding()) {
            EntityState state = TimeManager.popState(pEntity);
            if (state != null) {
                // 過去のステータスを強制適用
                TimeManager.applyState(pEntity, state);
                // 本来のTick処理（移動やダメージ計算）をキャンセル
                ci.cancel();
            } else {
                // 履歴が尽きた場合、プレイヤーなら逆行終了（停止音などはTimeManager側で制御）
                if (pEntity instanceof Player) {
                    TimeManager.forceNormalize();
                }
            }
            return; // 逆行中はここで終了
        }
        // --- 2. 停止（Time Stop）処理 ---
        if (TimeManager.isTimeStopped()) {
            if (!TimeManager.isImmune(pEntity)) {
                // 慣性を消す（ガタつき防止）
                pEntity.setDeltaMovement(Vec3.ZERO);
                ci.cancel();
                return;
            }
            // 免役持ち（プレイヤー等）は止まらずに次の処理（記録または加速）へ
        }
        // --- 3. 記録（通常・加速・停止中の免役持ち） ---
        // 逆行・停止（非免役）以外の時は常に履歴を記録し続ける
        TimeManager.recordState(pEntity);
        // --- 4. 加速（Acceleration）処理 ---
        int factor = TimeManager.getAccelerationFactor(pEntity);
        if (factor > 1 && !TimeManager.isTimeStopped()) {
            for (int i = 0; i < factor; i++) {
                // 補間用座標の更新
                pEntity.xo = pEntity.getX();
                pEntity.yo = pEntity.getY();
                pEntity.zo = pEntity.getZ();
                pEntity.yRotO = pEntity.getYRot();
                pEntity.xRotO = pEntity.getXRot();
                pConsumerEntity.accept(pEntity);
            }
            ci.cancel();
        }
    }
}