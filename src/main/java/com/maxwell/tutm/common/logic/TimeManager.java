package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.entity.TemporalLaserEntity;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.common.util.CurioHelper;
import com.maxwell.tutm.init.ModItems;
import com.maxwell.tutm.init.ModSounds;
import com.maxwell.tutm.mixin.ChunkMapAccessor;
import com.maxwell.tutm.mixin.ServerLevelAccessor;
import com.maxwell.tutm.mixin.TrackedEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimeManager {
    private static final Map<UUID, Deque<EntityState>> HISTORY = new ConcurrentHashMap<>();
    private static boolean serverTimeStopped = false;
    private static boolean serverRewinding = false;
    private static int serverAccelFactor = 1;
    private static int bossTimeStopTicks = 0;
    private static boolean clientTimeStopped = false;
    private static boolean clientRewinding = false;
    private static int clientAccelFactor = 1;

    public static boolean isTimeStopped() {
        return EffectiveSide.get().isClient() ? clientTimeStopped : serverTimeStopped;
    }

    public static boolean isRewinding() {
        return EffectiveSide.get().isClient() ? clientRewinding : serverRewinding;
    }

    public static int getAccelerationFactor(Entity e) {
        if (!isImmune(e)) return 1;
        return EffectiveSide.get().isClient() ? clientAccelFactor : serverAccelFactor;
    }

    public static void setAccelerationFactor(Player player, int factor) {
        if (!player.level().isClientSide) {
            serverAccelFactor = factor;
        }
    }

    /**
     * サーバー側の全プレイヤーティック更新
     */
    public static void serverTick(ServerPlayer player) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            data.tier = CurioHelper.getEquippedTankTier(player);
            if (data.tier > 0) {
                if (serverTimeStopped || serverRewinding || serverAccelFactor > 1) {
                    double cost = (serverTimeStopped ? 20 : 0) + (serverRewinding ? 50 : 0) + Math.pow(serverAccelFactor, 1.5);
                    data.currentCost = Math.max(0, data.currentCost - cost);
                    if (data.currentCost <= 0) forceNormalize();
                } else {
                    data.currentCost = Math.min(data.getMaxCost(), data.currentCost + 5000);
                }
            } else {
                data.currentCost = 0;
                if (serverTimeStopped || serverRewinding) forceNormalize();
            }
            TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new S2CSyncTimePacket(data.currentCost, data.getMaxCost(), data.tier, serverTimeStopped, serverAccelFactor, serverRewinding));
        });
    }

    /**
     * ボスやデバッグ用の時間停止開始
     */
    public static void startBossTimeStop(ServerLevel level, int duration) {
        serverTimeStopped = true;
        bossTimeStopTicks = duration;
        level.playSound(null, 0, 60, 0, ModSounds.TIME_STOP.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
    }

    /**
     * ボス加速のセット
     */
    public static void setBossAcceleration(int factor) {
        serverAccelFactor = factor;
    }

    public static void forceNormalize() {
        serverTimeStopped = false;
        serverRewinding = false;
        serverAccelFactor = 1;
        bossTimeStopTicks = 0;
    }

    public static void setClientState(boolean stopped, int accel, boolean rewind) {
        clientTimeStopped = stopped;
        clientAccelFactor = accel;
        clientRewinding = rewind;
    }

    public static void recordState(Entity entity) {
        if (isTimeStopped() || isRewinding()) return;
        Deque<EntityState> states = HISTORY.computeIfAbsent(entity.getUUID(), k -> new ArrayDeque<>());
        states.addFirst(new EntityState(entity.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(),
                (entity instanceof LivingEntity le) ? le.getHealth() : 0, (entity instanceof LivingEntity le) ? le.getAbsorptionAmount() : 0,
                entity.getRemainingFireTicks(), entity.getAirSupply(), entity.fallDistance));
        if (states.size() > 400) states.removeLast();
    }

    public static EntityState popState(Entity entity) {
        Deque<EntityState> states = HISTORY.get(entity.getUUID());
        if (states == null || states.isEmpty()) return null;
        synchronized (states) {
            return states.pollFirst();
        }
    }

    public static void applyState(Entity entity, EntityState state) {
        if (state == null) return;
        entity.setDeltaMovement(state.delta());
        entity.absMoveTo(state.pos().x, state.pos().y, state.pos().z, state.yRot(), state.xRot());
        if (entity instanceof LivingEntity le) {
            le.setHealth(state.health());
            le.setAbsorptionAmount(state.absorption());
        }
        entity.setRemainingFireTicks(state.fireTicks());
        entity.setAirSupply(state.airSupply());
        entity.fallDistance = state.fallDist();
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.yRotO = entity.getYRot();
        entity.xRotO = entity.getXRot();
    }

    /**
     * 逆行実行：Mixinから呼ばれる
     */
    public static void handleRewindTick(ServerLevel level) {
        ((ServerLevelAccessor) level).getEntityTickList().forEach(entity -> {
            EntityState state = popState(entity);
            if (state != null) {
                applyState(entity, state);
            } else if (entity instanceof Player) {
                forceNormalize();
            }
        });
    }

    /**
     * 時間停止中のティック処理：Mixinから呼ばれる
     */
    public static void tickImmuneEntitiesOnly(ServerLevel level) {
        if (bossTimeStopTicks > 0) {
            bossTimeStopTicks--;
            if (bossTimeStopTicks <= 0) {
                serverTimeStopped = false;
                level.playSound(null, 0, 60, 0, ModSounds.TIME_START.get(), SoundSource.HOSTILE, 1.0f, 1.0f);
            }
        }
        var entityMap = ((ChunkMapAccessor) level.getChunkSource().chunkMap).getEntityMap();
        ((ServerLevelAccessor) level).getEntityTickList().forEach(entity -> {
            if (entity.isRemoved()) return;
            if (isImmune(entity)) {
                level.guardEntityTick(level::tickNonPassenger, entity);
                Object tracked = entityMap.get(entity.getId());
                if (tracked != null) {
                    ((TrackedEntityAccessor) tracked).getServerEntity().sendChanges();
                }
            } else {
                entity.setPos(entity.xo, entity.yo, entity.zo);
                entity.setDeltaMovement(0, 0, 0);
            }
        });
    }

    public static boolean isImmune(Entity entity) {
        if (entity == null) return false;
        if (entity instanceof The_Ultimate_TimeManagerEntity) return true;
        if (entity instanceof TemporalLaserEntity) return false;
        if (entity instanceof Player p) {
            if (p.isCreative() || p.isSpectator() || p.getMainHandItem().getItem() == ModItems.DEBUG.get()) return true;
            return p.getCapability(TimeDataCapability.INSTANCE).map(data -> {
                if (isTimeStopped()) return data.tier >= 2 && data.currentCost > 0;
                return data.currentCost > 0;
            }).orElse(false);
        }
        return false;
    }

    public static void requestStop(Player p) {
        if (!p.level().isClientSide) serverTimeStopped = !serverTimeStopped;
    }

    public static void requestRewind(Player p) {
        if (!p.level().isClientSide) serverRewinding = !serverRewinding;
    }
}