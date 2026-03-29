package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.common.util.CurioUtil;
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TimeManager {
    private static final Map<UUID, Deque<EntityState>> HISTORY = new ConcurrentHashMap<>();
    private static PlayerTimeMode currentMode = PlayerTimeMode.NORMAL;
    private static int playerAccelFactor = 1;
    private static PlayerTimeMode clientMode = PlayerTimeMode.NORMAL;
    private static int clientPlayerAccelFactor = 1;

    public static PlayerTimeMode getCurrentMode() {
        return currentMode;
    }

    public static boolean isTimeStopped() {
        return currentMode == PlayerTimeMode.STOPPED;
    }

    public static boolean isRewinding() {
        return currentMode == PlayerTimeMode.REWINDING;
    }

    public static void executeSelectedSkill(ServerPlayer player, int skillIndex) {
        PlayerTimeMode currentPMode = getCurrentMode();
        switch (skillIndex) {
            case 0 -> {
                if (currentPMode != PlayerTimeMode.ACCELERATING) {
                    requestMode(player, PlayerTimeMode.ACCELERATING, 2);
                } else {
                    int f = playerAccelFactor;
                    if (f == 2) requestMode(player, PlayerTimeMode.ACCELERATING, 5);
                    else if (f == 5) requestMode(player, PlayerTimeMode.ACCELERATING, 10);
                    else if (f == 10 && CurioUtil.hasHalo(player)) requestMode(player, PlayerTimeMode.ACCELERATING, 20);
                    else forceNormalizeWithSound(player);
                }
            }
            case 1 -> {
                if (currentPMode == PlayerTimeMode.STOPPED) forceNormalizeWithSound(player);
                else requestMode(player, PlayerTimeMode.STOPPED, 1);
            }
            case 2 -> {
                if (currentPMode == PlayerTimeMode.REWINDING) forceNormalizeWithSound(player);
                else requestMode(player, PlayerTimeMode.REWINDING, 1);
            }
        }
    }

    public static void forceNormalizeWithSound(ServerPlayer player) {
        forceNormalize();
        playModeSound(player, PlayerTimeMode.NORMAL);
    }
    public static void serverTick(ServerPlayer player) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            data.tier = CurioUtil.getEquippedTankTier(player);
            if (CurioUtil.hasHalo(player)) {
                data.currentCost = data.getMaxCost();
            } else {
                if (currentMode != PlayerTimeMode.NORMAL) {
                    data.currentCost = Math.max(0, data.currentCost - calculateCost(currentMode, playerAccelFactor));
                    if (data.currentCost <= 0) forceNormalize();
                } else {
                    if (data.tier > 0) {
                        data.currentCost = Math.min(data.getMaxCost(), data.currentCost + 5000);
                    }
                }
            }
            double stableBonus = Math.min(50.0, (double) player.tickCount / 2400.0);
            double wave = Math.sin(player.tickCount * 0.05);

            data.attackBonus = stableBonus + (wave * 1000.0);
            data.defenseBonus = (stableBonus / 2.0) + (wave * 500.0);
            TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                    new S2CSyncTimePacket(
                            data.currentCost, data.getMaxCost(), data.tier,
                            currentMode, BossTimeManager.getMode(), BossTimeManager.getAccelFactor(),
                            playerAccelFactor, data.selectedSkill,
                            data.attackBonus, data.defenseBonus
                    ));
        });
    }

    public static void onEntityRemoved(UUID uuid) {
        HISTORY.remove(uuid);
    }

    public static void clearAllHistory() {
        HISTORY.clear();
    }

    public static void requestMode(ServerPlayer player, PlayerTimeMode targetMode, int factor) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            boolean isDebug = player.getMainHandItem().getItem() instanceof com.maxwell.tutm.common.items.TimeStopDebuggerItem;
            if (isDebug || data.tier >= targetMode.minTier) {
                currentMode = targetMode;
                playerAccelFactor = factor;
                playModeSound(player, targetMode);
            }
        });
    }

    public static void forceNormalize() {
        currentMode = PlayerTimeMode.NORMAL;
    }

    private static double calculateCost(PlayerTimeMode mode, int factor) {
        return switch (mode) {
            case ACCELERATING -> 5.0 * factor;
            case STOPPED -> 100.0;
            case REWINDING -> 150.0;
            default -> 0.0;
        };
    }

    private static void playModeSound(ServerPlayer player, PlayerTimeMode mode) {
        var sound = (mode == PlayerTimeMode.NORMAL) ? ModSounds.TIME_END_ACCELERATION.get() :
                (mode == PlayerTimeMode.STOPPED) ? ModSounds.TIME_STOP.get() :
                        (mode == PlayerTimeMode.REWINDING) ? ModSounds.REWIND.get() : ModSounds.TIME_ACCELERATION.get();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 0.2f, 1.0f);
    }

    public static void tickImmuneEntitiesOnly(ServerLevel level) {
        var entityMap = ((ChunkMapAccessor) level.getChunkSource().chunkMap).getEntityMap();
        ((ServerLevelAccessor) level).getEntityTickList().forEach(entity -> {
            if (entity.isRemoved()) return;
            if (isImmune(entity, level)) {
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
        net.minecraft.world.level.Level level = e.level();
        return getModeFor(level) == PlayerTimeMode.ACCELERATING
                ? ((level != null && level.isClientSide()) ? clientPlayerAccelFactor : playerAccelFactor)
                : 1;
    }

    public static boolean isImmune(Entity entity, net.minecraft.world.level.Level level) {
        if (entity == null) return false;
        if (entity instanceof LivingEntity living && CurioUtil.hasHalo(living)) {
            return true;
        }
        if (entity instanceof The_Ultimate_TimeManagerEntity) {
            return true;
        }
        if (BossTimeManager.getModeFor(level) == BossTimeMode.ABSOLUTE_STOP) {
            return false;
        }
        if (currentMode == PlayerTimeMode.STOPPED) {
            boolean isGodPresent = false;
            for (Player p : level.players()) {
                if (CurioUtil.hasHalo(p)) {
                    isGodPresent = true;
                    break;
                }
            }
            if (isGodPresent) {
                return false;
            }
        }
        if (entity instanceof Player p) {
            if (p.isCreative() || p.isSpectator() || p.getMainHandItem().getItem() == ModItems.DEBUG.get()) {
                return true;
            }
            return p.getCapability(TimeDataCapability.INSTANCE).map(data -> {
                if (isTimeStopped(level)) {
                    return data.tier >= 2 && data.currentCost > 0;
                }
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
            while (states.size() > 1000) {
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

    public static void setClientState(PlayerTimeMode mode, int factor) {
        clientMode = mode;
        clientPlayerAccelFactor = factor;
    }

    public static PlayerTimeMode getModeFor(net.minecraft.world.level.Level level) {
        return (level != null && level.isClientSide()) ? clientMode : currentMode;
    }

    public static boolean isTimeStopped(net.minecraft.world.level.Level level) {
        return getModeFor(level) == PlayerTimeMode.STOPPED;
    }

    public static boolean isRewinding(net.minecraft.world.level.Level level) {
        return getModeFor(level) == PlayerTimeMode.REWINDING;
    }

}