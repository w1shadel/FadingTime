package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.CurioUtil;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerTimeFlowMixin {
    @Shadow
    public abstract PlayerList getPlayerList();

    @Inject(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", at = @At("HEAD"))
    private void tutm$onAbsoluteServerTick(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        MinecraftServer server = (MinecraftServer) (Object) this;
        long currentRealTime = Util.getMillis();

        for (ServerLevel level : server.getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof The_Ultimate_TimeManagerEntity boss) {
                    boss.absoluteRealTimeTick(currentRealTime);
                }
            }
        }
    }
    @Redirect(
            method = "tickChildren",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V")
    )
    private void tutm$redirectLevelTick(ServerLevel serverlevel, BooleanSupplier pHasTimeLeft) {
        BossTimeManager.tick(serverlevel);

        if (serverlevel.dimension() == Level.OVERWORLD) {
            for (ServerPlayer player : this.getPlayerList().getPlayers()) {
                TimeManager.serverTick(player);
            }
        }
        BossTimeMode mode = BossTimeManager.getMode();

        if (mode != BossTimeMode.NORMAL) {
            switch (mode) {
                case STOPPED, ABSOLUTE_STOP -> {
                    TimeManager.tickImmuneEntitiesOnly(serverlevel);
                    return;
                }
                case ACCELERATING -> {
                    int factor = BossTimeManager.getAccelFactor();
                    for (int i = 0; i < factor; i++) {
                        serverlevel.tick(pHasTimeLeft);
                    }
                    return;
                }
            }
        }
        if (TimeManager.isTimeStopped(serverlevel)) {
            TimeManager.tickImmuneEntitiesOnly(serverlevel);
        } else {
            serverlevel.tick(pHasTimeLeft);
        }
    }
}