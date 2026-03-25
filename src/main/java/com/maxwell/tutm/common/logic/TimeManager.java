package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.init.ModItems;
import com.maxwell.tutm.init.ModSounds;
import com.maxwell.tutm.mixin.PostChainAccessor;
import com.maxwell.tutm.mixin.ServerLevelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTickList;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TimeManager {
    private static final double REGEN_PER_TICK = 100000.0;
    private static final int PLAYER_MAX_HISTORY = 360;
    private static final double STOP_START_COST = 50000.0;
    private static final double STOP_MAINTAIN_COST = 5.0;
    private static final double REWIND_START_COST = 100000.0;
    private static final double REWIND_MAINTAIN_COST = 20.0;
    private static final Map<UUID, Deque<EntityState>> HISTORY = new ConcurrentHashMap<>();
    private static boolean isTimeStopped = false;
    private static int accelerationFactor = 1;
    private static boolean isRewinding = false;
    private static float stopTransitionProgress = 0.0F;
    private static float realPartialTicks = 0.0F;
    private static double clientCost, clientMax;

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
                if (data.currentCost < data.maxCost) {
                    data.currentCost = Math.min(data.maxCost, data.currentCost + REGEN_PER_TICK);
                }
            }
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
            // Only shutdown if it's OUR shader. If it's a boss distortion or something else, leave it be.
            var chain = mc.gameRenderer.currentEffect();
            if (chain != null) {
                try {
                    var passes = ((PostChainAccessor) chain).getPasses();
                    boolean isMyShader = false;
                    for (var pass : passes) {
                        if (pass.getEffect().getName().contains("time_spatial")) {
                            isMyShader = true;
                            break;
                        }
                    }
                    if (isMyShader) {
                        mc.gameRenderer.shutdownEffect();
                    }
                } catch (Exception e) {
                    // Fallback to safety
                }
            }
            stopTransitionProgress = 0.0F;
        }
    }

    public static void requestStop(Player player) {
        if (player.level().isClientSide) return;
        if (isRewinding) {
            player.displayClientMessage(Component.literal("§c逆行中は時を止められません"), true);
            return;
        }
        if (accelerationFactor > 1) {
            player.displayClientMessage(Component.literal("§c加速中は時を止められません"), true);
            return;
        }
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (!isTimeStopped) {
                if (data.currentCost >= STOP_START_COST) {
                    data.currentCost -= STOP_START_COST;
                    isTimeStopped = true;
                    isRewinding = false;
                    accelerationFactor = 1;
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                            ModSounds.TIME_STOP.get(), SoundSource.PLAYERS, 0.3F, 1.0F);
                } else {
                    player.displayClientMessage(Component.literal("§cエネルギーが足りません"), true);
                }
            } else {
                isTimeStopped = false;
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.TIME_START.get(), SoundSource.PLAYERS, 0.2F, 1.0F);
            }
        });
    }

    public static void requestRewind(Player player) {
        if (player.level().isClientSide) return;
        if (isTimeStopped) {
            player.displayClientMessage(Component.literal("§c停止中は逆行できません"), true);
            return;
        }
        if (accelerationFactor > 1) {
            player.displayClientMessage(Component.literal("§c加速中は逆行できません"), true);
            return;
        }
        player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
            if (!isRewinding) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.REWIND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                if (data.currentCost >= REWIND_START_COST) {
                    data.currentCost -= REWIND_START_COST;
                    isRewinding = true;
                    isTimeStopped = false;
                    accelerationFactor = 1;
                } else {
                    player.displayClientMessage(Component.literal("§cエネルギーが足りません"), true);
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
            boolean hasItem = player.getMainHandItem().getItem() == ModItems.DEBUG.get() ||
                    player.getOffhandItem().getItem() == ModItems.DEBUG.get();
            if (hasItem) return true;
            if (entity.level().isClientSide) return player == Minecraft.getInstance().player;
        }
        return false;
    }

    private static void updateShaderUniforms() {
        var mc = Minecraft.getInstance();
        var postChain = mc.gameRenderer.currentEffect();
        if (postChain == null) return;
        List<PostPass> passes = ((PostChainAccessor) postChain).getPasses();
        float shaderMode = 0.0f;
        if (isRewinding) {
            shaderMode = 3.0f;
        } else if (isTimeStopped) {
            shaderMode = 2.0f;
        } else if (accelerationFactor > 1) {
            shaderMode = 1.0f;
        }
        for (PostPass pass : passes) {
            var shader = pass.getEffect();
            if (shader.getName().contains("time_spatial")) {
                shader.safeGetUniform("StopProgress").set(stopTransitionProgress);
                shader.safeGetUniform("Time").set((float) (System.currentTimeMillis() % 100000) / 1000.0F);
                shader.safeGetUniform("AccelFactor").set((float) accelerationFactor);
                shader.safeGetUniform("Mode").set(shaderMode);
            }
        }
    }

    public static EntityState popState(Entity entity) {
        Deque<EntityState> states = HISTORY.get(entity.getUUID());
        if (states == null) return null;
        synchronized (states) {
            return states.isEmpty() ? null : states.removeFirst();
        }
    }

    public static void setClientData(double c, double m, boolean s, int a, boolean r) {
        clientCost = c;
        clientMax = m;
        isTimeStopped = s;
        accelerationFactor = a;
        isRewinding = r;
    }

    public static float getRealPartialTicks() {
        return realPartialTicks;
    }

    public static void setRealPartialTicks(float f) {
        realPartialTicks = f;
    }

    public static boolean isTimeStopped() {
        return isTimeStopped;
    }

    public static boolean isRewinding() {
        return isRewinding;
    }

    public static void setAccelerationFactor(Player player, int f) {
        if (player.level().isClientSide) return;
        // 排他チェック：停止中や逆行中は加速させない
        if (isTimeStopped || isRewinding) {
            String power = isTimeStopped ? "停止" : "逆行";
            player.displayClientMessage(Component.literal("§c" + power + "中は加速できません"), true);
            accelerationFactor = 1;
            return;
        }
        int previousFactor = accelerationFactor;
        accelerationFactor = f;
        if (previousFactor == 1 && accelerationFactor > 1) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TIME_ACCELERATION.get(), SoundSource.PLAYERS, 0.1F, 1.0F);
            player.displayClientMessage(Component.literal("§e[時空制御] §f加速開始..."), true);
        } else if (previousFactor > 1 && accelerationFactor == 1) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TIME_END_ACCELERATION.get(), SoundSource.PLAYERS, 0.1F, 1.0F);
            player.displayClientMessage(Component.literal("§e[時空制御] §f加速終了"), true);
        }
    }

    public static int getAccelerationFactor(Entity e) {
        return isImmune(e) ? accelerationFactor : 1;
    }

    public static double getClientCostRatio() {
        return clientMax > 0 ? clientCost / clientMax : 0;
    }

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
                    applyState(entity, state);
                    entity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
                } else {
                    if (entity instanceof Player) {
                        forceNormalize();
                    }
                }
            }
        });
    }

    public static void recordState(Entity entity) {
        if (isRewinding || isTimeStopped) return;
        UUID uuid = entity.getUUID();
        Deque<EntityState> states = HISTORY.computeIfAbsent(uuid, k -> new ArrayDeque<>());
        synchronized (states) {
            states.addFirst(new EntityState(
                    entity.position(),
                    entity.getDeltaMovement(),
                    entity.getYRot(),
                    entity.getXRot(),
                    (entity instanceof LivingEntity le) ? le.getHealth() : 0.0F,
                    (entity instanceof LivingEntity le) ? le.getAbsorptionAmount() : 0.0F,
                    entity.getRemainingFireTicks(),
                    entity.getAirSupply(),
                    entity.fallDistance
            ));
            while (states.size() > PLAYER_MAX_HISTORY) {
                if (!states.isEmpty()) {
                    states.removeLast();
                } else {
                    break;
                }
            }
        }
    }

    public static void applyState(Entity entity, EntityState state) {
        if (state == null) return;
        entity.setDeltaMovement(state.delta());
        entity.xo = entity.getX();
        entity.yo = entity.getY();
        entity.zo = entity.getZ();
        entity.absMoveTo(state.pos().x, state.pos().y, state.pos().z, state.yRot(), state.xRot());
        if (entity instanceof LivingEntity le) {
            le.setHealth(state.health());
            le.setAbsorptionAmount(state.absorption());
        }
        entity.setRemainingFireTicks(state.fireTicks());
        entity.setAirSupply(state.airSupply());
        entity.fallDistance = state.fallDist();
    }
}