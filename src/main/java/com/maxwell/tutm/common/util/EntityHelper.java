package com.maxwell.tutm.common.util;

import com.maxwell.tutm.init.ModDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityHelper {
    private static final Map<UUID, Integer> INSTABILITY_COUNT = new HashMap<>();

    public static void applyAbsoluteTimeAttack(LivingEntity target, Entity owner, float amount) {
        if (target.level().isClientSide || !target.isAlive()) return;
        if (target instanceof Player p && (p.isCreative() || p.isSpectator())) return;
        if (owner == target) return;
        DamageSource source = ModDamageTypes.getTimeDamageSource(target.level(), owner);
        float healthBefore = target.getHealth();
        target.invulnerableTime = 0;
        boolean success = target.hurt(source, amount);
        float healthAfter = target.getHealth();
        if (target instanceof ServerPlayer serverPlayer) {
            UUID uuid = serverPlayer.getUUID();
            if (serverPlayer.isBlocking()) {
                INSTABILITY_COUNT.put(uuid, 0);
                return;
            }
            if (!success || healthAfter >= healthBefore) {
                int count = INSTABILITY_COUNT.getOrDefault(uuid, 0) + 1;
                INSTABILITY_COUNT.put(uuid, count);
                if (count >= 10) {
                    executeTimelineCorrection(serverPlayer);
                    INSTABILITY_COUNT.remove(uuid);
                }
            } else {
                INSTABILITY_COUNT.put(uuid, 0);
            }
        }
    }

    private static void executeTimelineCorrection(ServerPlayer player) {
        player.connection.disconnect(Component.literal("§0[SYSTEM_ERROR]\n§dDetected an unchangeable existence.\n§cYour armor cannot protect you from Time."));
    }
}