package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerTimeFlowMixin {
    @Shadow
    public abstract PlayerList getPlayerList();

    @Redirect(
            method = "tickChildren",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;tick(Ljava/util/function/BooleanSupplier;)V")
    )
    private void tutm$redirectLevelTick(ServerLevel serverlevel, BooleanSupplier pHasTimeLeft) {
        BossTimeManager.tick(serverlevel);
        for (ServerPlayer player : this.getPlayerList().getPlayers()) {
            TimeManager.serverTick(player);
        }
        if (BossTimeManager.getMode() != BossTimeMode.NORMAL) {
            switch (BossTimeManager.getMode()) {
                case STOPPED, ABSOLUTE_STOP -> TimeManager.tickImmuneEntitiesOnly(serverlevel);
                case ACCELERATING -> {
                    int factor = BossTimeManager.getAccelFactor();
                    for (int i = 0; i < factor; i++) {
                        serverlevel.tick(pHasTimeLeft);
                    }
                }
            }
            return;
        }
        if (TimeManager.isTimeStopped()) {
            TimeManager.tickImmuneEntitiesOnly(serverlevel);
        } else if (TimeManager.isRewinding()) {
            serverlevel.tick(pHasTimeLeft);
        } else {
            serverlevel.tick(pHasTimeLeft);
        }

    }
}