package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.CurioUtil;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.common.world.TimeRealmGenerator;
import com.maxwell.tutm.common.world.TimeRealmInitializer;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    private static final double VOID_Y = -10;
    private static final double EXIT_Y = -64;
    private static final String STRUCTURE_PATH = "tutm_house";
    private static final BlockPos TARGET_POS = new BlockPos(666, 66, 666);

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide()) return;
        ServerPlayer player = (ServerPlayer) event.player;
        if (player.level().dimension().equals(Level.END)) {
            if (player.getY() < VOID_Y) {
                int playerAccel = TimeManager.getPlayerAccelerationFactor(player);
                int bossAccel = BossTimeManager.getAccelFactor();
                int totalAccel = Math.max(playerAccel, bossAccel);
                if (totalAccel >= 8) {
                    moveToTimeRealm(player);
                }
            }
        }
        if (player.level().dimension().equals(TUTMDimensions.TIME_REALM_LEVEL_KEY)) {
            if (player.getY() < EXIT_Y) {
                returnToOverworld(player);
            }
        }
    }

    private static void moveToTimeRealm(ServerPlayer player) {
        ServerLevel destination = player.server.getLevel(TUTMDimensions.TIME_REALM_LEVEL_KEY);
        if (destination != null) {
            double destX = 0.5;
            double destY = 65.0;
            double destZ = -45.0;
            player.fallDistance = 0;
            player.setDeltaMovement(0, 0, 0);
            player.teleportTo(destination, destX, destY, destZ, 0, 0);
            TimeRealmGenerator.generateArena(destination, new BlockPos(0, 60, 0));
            TimeRealmInitializer.placeStructure(destination, TARGET_POS, STRUCTURE_PATH);
        }
    }

    private static void returnToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        if (overworld != null) {
            BlockPos respawnPos = player.getRespawnPosition();
            if (respawnPos == null) {
                respawnPos = overworld.getSharedSpawnPos();
            }
            player.fallDistance = 0;
            player.setDeltaMovement(0, 0, 0);
            player.teleportTo(overworld, respawnPos.getX() + 0.5, respawnPos.getY() + 1.0, respawnPos.getZ() + 0.5, player.getYRot(), player.getXRot());
            player.setInvulnerable(false);
            player.hurtMarked = true;
        }
    }

    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            TimeManager.onEntityRemoved(entity.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            TimeManager.clearAllHistory();
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(TimeDataCapability.Provider.IDENTIFIER, new TimeDataCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(TimeDataCapability.INSTANCE).ifPresent(oldData -> {
                event.getEntity().getCapability(TimeDataCapability.INSTANCE).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (CurioUtil.hasHalo(player)) {
                if (!player.getCooldowns().isOnCooldown(ModItems.TIME_HALO.get())) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.getCooldowns().addCooldown(ModItems.TIME_HALO.get(), 1200);
                }
            }
        }
    }
}