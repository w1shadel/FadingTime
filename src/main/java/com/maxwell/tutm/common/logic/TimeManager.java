package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
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
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimeManager {
    private static final Map<UUID, Deque<EntityState>> HISTORY = new ConcurrentHashMap<>();
    private static PlayerTimeMode currentMode = PlayerTimeMode.NORMAL;
    private static int activeTicks = 0;
    private static int playerAccelFactor = 1;
    public static PlayerTimeMode getCurrentMode() { return currentMode; }
    public static boolean isTimeStopped() { return currentMode == PlayerTimeMode.STOPPED; }
    public static boolean isRewinding() { return currentMode == PlayerTimeMode.REWINDING; }

    public static void serverTick(ServerPlayer player) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (currentMode != PlayerTimeMode.NORMAL) {
                data.currentCost = Math.max(0, data.currentCost - calculateCost(currentMode));
                if (data.currentCost <= 0 || activeTicks <= 0) {
                    forceNormalize();
                } else {
                    activeTicks--;
                }
            } else {
                data.currentCost = Math.min(data.getMaxCost(), data.currentCost + 5000);
            }
            TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new S2CSyncTimePacket(
                            data.currentCost,
                            data.getMaxCost(),
                            data.tier,
                            currentMode,              // プレイヤーモード
                            BossTimeManager.getMode(), // ボスモード
                            BossTimeManager.getAccelFactor() // ボス加速倍率
                    ));
        });
    }

    public static void onEntityRemoved(UUID uuid) {
        HISTORY.remove(uuid);
    }
    public static void clearAllHistory() {
        HISTORY.clear();
    }
    public static void requestMode(ServerPlayer player, PlayerTimeMode targetMode, int duration, int factor) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (data.tier < targetMode.minTier) return;
            currentMode = targetMode;
            activeTicks = duration;
            playerAccelFactor = factor;
            playModeSound(player, targetMode);
        });
    }
    public static void requestMode(ServerPlayer player, PlayerTimeMode targetMode, int duration) {
        requestMode(player, targetMode, duration, 1);
    }
    public static void forceNormalize() {
        currentMode = PlayerTimeMode.NORMAL;
        activeTicks = 0;
    }

    private static double calculateCost(PlayerTimeMode mode) {
        return switch (mode) {
            case ACCELERATING -> 1.5;
            case STOPPED -> 20.0;
            case REWINDING -> 50.0;
            default -> 0.0;
        };
    }

    private static void playModeSound(ServerPlayer player, PlayerTimeMode mode) {
        var sound = (mode == PlayerTimeMode.NORMAL) ? ModSounds.TIME_END_ACCELERATION.get() :
                (mode == PlayerTimeMode.STOPPED) ? ModSounds.TIME_STOP.get() :
                        (mode == PlayerTimeMode.REWINDING) ? ModSounds.REWIND.get() : ModSounds.TIME_ACCELERATION.get();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 0.5f, 1.0f);
    }
    public static void tickImmuneEntitiesOnly(ServerLevel level) {

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
    public static int getPlayerAccelerationFactor(Entity e) {
        return (currentMode == PlayerTimeMode.ACCELERATING) ? playerAccelFactor : 1;
    }

    public static boolean isImmune(Entity entity) {
        if (entity == null) return false;
        if (entity instanceof The_Ultimate_TimeManagerEntity) return true;
        if (entity instanceof Player p) {
            if (p.isCreative() || p.isSpectator() || p.getMainHandItem().getItem() == ModItems.DEBUG.get()) return true;
            return p.getCapability(TimeDataCapability.INSTANCE).map(data -> {
                if (isTimeStopped()) return data.tier >= 2 && data.currentCost > 0;
                return data.currentCost > 0;
            }).orElse(false);
        }
        return false;
    }
    public static void recordState(Entity entity) {
        if (entity.level().isClientSide) return;
        if (isTimeStopped() || isRewinding()) return;
        Deque<EntityState> states = HISTORY.computeIfAbsent(entity.getUUID(), k -> new ArrayDeque<>());
        synchronized (states) {
            states.addFirst(new EntityState(
                    entity.position(),
                    entity.getDeltaMovement(),
                    entity.getYRot(),
                    entity.getXRot(),
                    (entity instanceof LivingEntity le) ? le.getHealth() : 0,
                    (entity instanceof LivingEntity le) ? le.getAbsorptionAmount() : 0,
                    entity.getRemainingFireTicks(),
                    entity.getAirSupply(),
                    entity.fallDistance
            ));
            while (states.size() > 400) {
                states.removeLast();
            }
        }
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

}