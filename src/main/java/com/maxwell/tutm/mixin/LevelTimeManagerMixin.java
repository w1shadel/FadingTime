package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.EntityState;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(Level.class)
public abstract class LevelTimeManagerMixin {
    @Unique
    private static boolean tutm$isProcessing = false;

    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void tutm$manageFlow(Consumer<T> consumer, T entity, CallbackInfo ci) {
        if (tutm$isProcessing) return;
        if (TimeManager.isTimeStopped() && !TimeManager.isImmune(entity)) {
            if (entity.tickCount > 0) {
                entity.setPos(entity.xo, entity.yo, entity.zo);
                entity.setDeltaMovement(0, 0, 0);
            }
            ci.cancel();
            return;
        }
        if (TimeManager.isRewinding()) {
            EntityState s = TimeManager.popState(entity);
            if (s != null) {
                entity.absMoveTo(s.pos().x, s.pos().y, s.pos().z, s.yRot(), s.xRot());
                entity.setDeltaMovement(0, 0, 0);
                ci.cancel();
                return;
            } else if (entity instanceof Player) {
                TimeManager.forceNormalize();
            }
        }
        TimeManager.recordState(entity);
        int factor = (TimeManager.isImmune(entity)) ? TimeManager.getAccelerationFactor(entity) : 1;
        if (factor > 1) {
            tutm$isProcessing = true;
            for (int i = 0; i < factor - 1; i++) {
                entity.xo = entity.getX();
                entity.yo = entity.getY();
                entity.zo = entity.getZ();
                entity.yRotO = entity.getYRot();
                entity.xRotO = entity.getXRot();
                consumer.accept(entity);
            }
            tutm$isProcessing = false;
        }
    }
}