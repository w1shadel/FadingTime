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
        // 1. 時間逆行
        if (TimeManager.isRewinding()) {
            EntityState state = TimeManager.popState(pEntity);
            if (state != null) {
                TimeManager.applyState(pEntity, state);
                ci.cancel();
            } else if (pEntity instanceof Player) {
                TimeManager.forceNormalize();
            }
            return;
        }

        // 2. 時間停止
        if (TimeManager.isTimeStopped()) {
            if (!TimeManager.isImmune(pEntity)) {
                // 免疫がない相手は座標を固定して完全に止める
                pEntity.setPos(pEntity.xo, pEntity.yo, pEntity.zo);
                pEntity.setDeltaMovement(0, 0, 0);
                ci.cancel();
                return;
            }
        }

        // 3. 状態記録（逆行用）
        TimeManager.recordState(pEntity);

        // 4. 加速処理（免疫がある相手、または停止していない時のみ）
        // 停止中でもボス（Immune）ならここを通るようにする
        int factor = TimeManager.getAccelerationFactor(pEntity);
        if (factor > 1) {
            for (int i = 0; i < factor - 1; i++) {
                // 重要：次の accept(tick) の前に、現在の位置を「古い位置」として保存する
                // これをしないと、クライアント側で補間（Interpolation）ができず瞬間移動に見える
                pEntity.xo = pEntity.getX();
                pEntity.yo = pEntity.getY();
                pEntity.zo = pEntity.getZ();
                pEntity.yRotO = pEntity.getYRot();
                pEntity.xRotO = pEntity.getXRot();

                pConsumerEntity.accept(pEntity);
            }
            // 最後の1回は通常通り外側で実行される
        }
    }
}