package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.init.ModItem;
import com.maxwell.tutm.mixin.PostChainAccessor;
import com.maxwell.tutm.mixin.ServerLevelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class TimeManager {

    private static boolean isTimeStopped = false;
    private static int accelerationFactor = 1;
    private static boolean isRewinding = false;
    private static float stopTransitionProgress = 0.0F;
    private static float realPartialTicks = 0.0F;
    private static double clientCost, clientMax;
    private static final double REGEN_PER_TICK = 100000.0;

    private static final double STOP_START_COST = 50000.0;
    private static final double STOP_MAINTAIN_COST = 5.0;
    private static final double REWIND_START_COST = 100000.0;
    private static final double REWIND_MAINTAIN_COST = 20.0;

    private static final Map<UUID, Deque<EntityState>> HISTORY = new HashMap<>();

    public static void serverTick(ServerPlayer player) {
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            boolean isUsingTime = isTimeStopped || isRewinding || accelerationFactor > 1;

            if (isUsingTime) {
                double consumption = calculateConsumption();
                if (data.currentCost >= consumption) {
                    data.currentCost -= consumption;
                } else {
                    forceNormalize();
                }
            } else {
                // 非使用時：回復
                if (data.currentCost < data.maxCost) {
                    data.currentCost = Math.min(data.maxCost, data.currentCost + REGEN_PER_TICK);
                }
            }

            // 同期パケットの送信
            TUTMPacketHandler.INSTANCE.send(
                    net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                    new S2CSyncTimePacket(data.currentCost, data.maxCost, isTimeStopped, accelerationFactor, isRewinding)
            );
        });
    }
    private static double calculateConsumption() {
        double cost = 0;
        if (isTimeStopped) cost += STOP_MAINTAIN_COST;
        if (isRewinding) cost += REWIND_MAINTAIN_COST;
        if (accelerationFactor > 1) cost += Math.pow(accelerationFactor, 2);
        return cost;
    }

    public static void updateClientEffects() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gameRenderer == null) return;
        if (isTimeStopped || isRewinding || accelerationFactor > 1) {
            if (mc.gameRenderer.currentEffect() == null) {
                mc.gameRenderer.loadEffect(new ResourceLocation("tutm", "shaders/post/time_spatial.json"));
            }
            updateShaderUniforms();
            if (isTimeStopped && stopTransitionProgress < 1.0F) {
                stopTransitionProgress += 0.05F;
            }
        } else {
            if (mc.gameRenderer.currentEffect() != null) mc.gameRenderer.shutdownEffect();
            stopTransitionProgress = 0.0F;
        }
    }
    public static void requestStop(Player player) {
        if (player.level().isClientSide) return;
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (!isTimeStopped) {
                if (data.currentCost >= STOP_START_COST) {
                    data.currentCost -= STOP_START_COST;
                    isTimeStopped = true;
                    isRewinding = false;
                }
            } else {
                isTimeStopped = false;
            }
        });
    }
    private static void updateShaderUniforms() {
        var mc = Minecraft.getInstance();
        var postChain = mc.gameRenderer.currentEffect();
        if (postChain == null) return;
        List<PostPass> passes = ((PostChainAccessor) postChain).getPasses();

        for (PostPass pass : passes) {
            var shader = pass.getEffect();
            if (shader.getName().contains("time_spatial")) {
                shader.safeGetUniform("StopProgress").set(stopTransitionProgress);
            }
        }
    }
    private static int getShaderMode() {
        if (isRewinding) return 3;
        if (isTimeStopped) return 2;
        if (accelerationFactor > 1) return 1;
        return 0;
    }
    public static void requestRewind(Player player) {
        if (player.level().isClientSide) return;
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (!isRewinding) {
                if (data.currentCost >= REWIND_START_COST) {
                    data.currentCost -= REWIND_START_COST;
                    isRewinding = true;
                    isTimeStopped = false;
                }
            } else {
                isRewinding = false;
            }
        });
    }

    public static void forceNormalize() {
        isTimeStopped = false;
        isRewinding = false;
        accelerationFactor = 1;
    }

    public static boolean isImmune(Entity entity) {
        if (entity == null) return false;
        if (entity instanceof The_Ultimate_TimeManagerEntity) return true;
        if (entity instanceof Player player) {
            boolean hasItem = player.getMainHandItem().getItem() == ModItem.DEBUG.get() ||
                    player.getOffhandItem().getItem() == ModItem.DEBUG.get();
            if (hasItem) return true;
            if (entity.level().isClientSide) return player == Minecraft.getInstance().player;
        }
        return false;
    }

    public static void recordState(Entity entity) {
        if (isRewinding || isTimeStopped) return;
        Deque<EntityState> states = HISTORY.computeIfAbsent(entity.getUUID(), k -> new ArrayDeque<>());
        states.addFirst(new EntityState(entity.position(), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot(),
                (entity instanceof LivingEntity le) ? le.getHealth() : 0.0F));
        if (states.size() > 200) states.removeLast();
    }

    public static EntityState popState(Entity entity) {
        Deque<EntityState> states = HISTORY.get(entity.getUUID());
        return (states == null || states.isEmpty()) ? null : states.removeFirst();
    }

    public static void setClientData(double c, double m, boolean s, int a, boolean r) {
        clientCost = c; clientMax = m; isTimeStopped = s; accelerationFactor = a; isRewinding = r;
    }

    public static void setRealPartialTicks(float f) { realPartialTicks = f; }
    public static float getRealPartialTicks() { return realPartialTicks; }
    public static boolean isTimeStopped() { return isTimeStopped; }
    public static boolean isRewinding() { return isRewinding; }
    public static int getAccelerationFactor(Entity e) { return isImmune(e) ? accelerationFactor : 1; }
    public static double getClientCostRatio() { return clientMax > 0 ? clientCost / clientMax : 0; }
    public static float getStopTransition() { return stopTransitionProgress; }
    public static void setAccelerationFactor(int f) { accelerationFactor = f; }
    public static void tickImmuneEntitiesOnly(ServerLevel level) {
        EntityTickList tickList = ((ServerLevelAccessor) level).getEntityTickList();
        tickList.forEach(entity -> {
            if (!entity.isRemoved() && isImmune(entity)) {
                level.guardEntityTick(level::tickNonPassenger, entity);
            } else {
                entity.xo = entity.getX();
                entity.yo = entity.getY();
                entity.zo = entity.getZ();
                entity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
            }
        });
    }
    public static void handleRewindTick(ServerLevel level) {
        EntityTickList tickList = ((ServerLevelAccessor) level).getEntityTickList();
        tickList.forEach(entity -> {
            if (!entity.isRemoved()) {
                var state = popState(entity);
                if (state != null) {
                    entity.absMoveTo(state.pos().x, state.pos().y, state.pos().z, state.yRot(), state.xRot());
                    if (entity instanceof LivingEntity le) le.setHealth(state.health());
                    entity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                }
            }
        });
    }
}