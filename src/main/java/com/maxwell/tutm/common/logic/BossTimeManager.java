package com.maxwell.tutm.common.logic;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.network.S2CSyncTimePacket;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class BossTimeManager {
    private static BossTimeMode currentMode = BossTimeMode.NORMAL;
    private static int bossTicks = 0;
    private static int bossAccelFactor = 1;
    private static BossTimeMode clientMode = BossTimeMode.NORMAL;
    private static int clientBossAccelFactor = 1;

    public static BossTimeMode getMode() {
        return currentMode;
    }

    public static int getAccelFactor() {
        return bossAccelFactor;
    }

    public static void setAccelFactor(int factor) {
        bossAccelFactor = factor;
    }

    public static boolean isTimeStopped() {
        return currentMode != BossTimeMode.NORMAL;
    }

    public static void requestBossMode(ServerLevel level, BossTimeMode mode, int duration, int factor) {
        currentMode = mode;
        bossTicks = duration;
        bossAccelFactor = factor;
        syncAll(level.getServer());
    }

    public static int getBossAccelerationFactor() {
        return (currentMode == BossTimeMode.ACCELERATING) ? bossAccelFactor : 1;
    }

    public static void tick(ServerLevel level) {
        if (currentMode != BossTimeMode.NORMAL) {
            bossTicks--;
            if (bossTicks <= 0) {
                currentMode = BossTimeMode.NORMAL;
                bossAccelFactor = 1;
                syncAll(level.getServer());
            }
        }
    }

    private static void syncAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                double maxCost = data.getMaxCost(player);

                TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player),
                        new S2CSyncTimePacket(
                                data.currentCost,
                                maxCost,
                                data.tier,
                                TimeManager.getCurrentMode(),
                                currentMode,
                                bossAccelFactor,
                                TimeManager.getPlayerAccelerationFactor(player),
                                data.selectedSkill,
                                data.attackBonus,
                                data.defenseBonus
                        ));
            });
        }
    }

    public static void setClientState(BossTimeMode mode, int factor) {
        clientMode = mode;
        clientBossAccelFactor = factor;
    }

    public static BossTimeMode getModeFor(net.minecraft.world.level.Level level) {
        return (level != null && level.isClientSide()) ? clientMode : currentMode;
    }

    public static int getAccelFactorFor(net.minecraft.world.level.Level level) {
        return (level != null && level.isClientSide()) ? clientBossAccelFactor : bossAccelFactor;
    }

    public static boolean isTimeStopped(net.minecraft.world.level.Level level) {
        return getModeFor(level) != BossTimeMode.NORMAL;
    }

}