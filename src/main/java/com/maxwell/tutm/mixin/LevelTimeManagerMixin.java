package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.*;
import com.maxwell.tutm.common.util.CurioUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = Level.class)
public abstract class LevelTimeManagerMixin {
    @Unique
    private static boolean tutm$isProcessing = false;

    @Inject(method = "guardEntityTick", at = @At("HEAD"), cancellable = true)
    private void tutm$manageFlow(Consumer<Entity> consumer, Entity entity, CallbackInfo ci) {
        if (tutm$isProcessing) return;
        boolean isServer = !entity.level().isClientSide();
        Level level = entity.level();
        BossTimeMode bMode = BossTimeManager.getModeFor(level);
        PlayerTimeMode pMode = TimeManager.getModeFor(level);
        boolean isAbsoluteStop = bMode == BossTimeMode.ABSOLUTE_STOP;
        boolean isBossStopping = bMode == BossTimeMode.STOPPED || isAbsoluteStop;
        boolean isPlayerStopping = pMode == PlayerTimeMode.STOPPED;
        boolean isTimeStopped = isBossStopping || isPlayerStopping;
        int factor = bMode == BossTimeMode.ACCELERATING
                ? BossTimeManager.getAccelFactorFor(level)
                : TimeManager.getPlayerAccelerationFactor(entity);
        boolean isImmune = false;
        if (isAbsoluteStop) {
            isImmune = (entity instanceof The_Ultimate_TimeManagerEntity) || (entity instanceof LivingEntity living && CurioUtil.hasHalo(living));
        } else if (isBossStopping) {
            isImmune = (entity instanceof The_Ultimate_TimeManagerEntity) || (entity instanceof LivingEntity living && CurioUtil.hasHalo(living));
        } else if (isPlayerStopping) {
            isImmune = TimeManager.isImmune(entity, level);
        }
        if (isTimeStopped && !isImmune) {
            entity.setPos(entity.xo, entity.yo, entity.zo);
            entity.setDeltaMovement(0, 0, 0);
            ci.cancel();
            return;
        }
        if (pMode == PlayerTimeMode.REWINDING && !isBossStopping) {
            if (!isImmune || entity instanceof Player) {
                if (isServer) {
                    EntityState s = TimeManager.popState(entity);
                    if (s != null) {
                        TimeManager.applyState(entity, s);
                        if (entity instanceof ServerPlayer sp) {
                            sp.connection.teleport(s.pos().x, s.pos().y, s.pos().z, s.yRot(), s.xRot());
                        } else if (level instanceof ServerLevel sl) {
                            var entityMap = ((ChunkMapAccessor) sl.getChunkSource().chunkMap).getEntityMap();
                            Object tracked = entityMap.get(entity.getId());
                            if (tracked != null) {
                                ((TrackedEntityAccessor) tracked).getServerEntity().sendChanges();
                            }
                        }
                    } else if (entity instanceof Player) {
                        TimeManager.forceNormalize();
                    }
                }
                if (!(entity instanceof Player && !isServer)) {
                    entity.setPos(entity.xo, entity.yo, entity.zo);
                    entity.setDeltaMovement(0, 0, 0);
                    ci.cancel();
                    return;
                }
            }
        }
        if (factor > 1 && bMode != BossTimeMode.ACCELERATING) {
            if (isImmune || entity instanceof Player) {
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
        if (isServer) {
            tutm$internalCollectDebt(entity);
            TimeManager.recordState(entity);
        }
    }

    @Inject(method = "guardEntityTick", at = @At("TAIL"))
    private void tutm$collectDebtAfter(Consumer<Entity> consumer, Entity entity, CallbackInfo ci) {
        if (!entity.level().isClientSide()) {
            tutm$internalCollectDebt(entity);
        }
    }

    @Unique
    private void tutm$internalCollectDebt(Entity entity) {
        if (entity instanceof LivingEntity living && living.getPersistentData().contains("TimeDebt")) {
            float debt = living.getPersistentData().getFloat("TimeDebt");
            if (debt > 0) {
                float newHealth = living.getHealth() - debt;
                try {
                    var healthId = LivingEntityAccessor.getHealthDataId();
                    living.getEntityData().set(healthId, newHealth);
                } catch (Exception e) {
                    living.setHealth(newHealth);
                }
                if (newHealth <= 0) {
                    living.setRemoved(Entity.RemovalReason.KILLED);
                }
                living.getPersistentData().remove("TimeDebt");
            }
        }
    }
}