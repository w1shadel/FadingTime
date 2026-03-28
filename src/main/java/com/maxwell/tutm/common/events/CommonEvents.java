package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.PlayerTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.world.TUTMDimensions;
import com.maxwell.tutm.common.world.TimeRealmGenerator;
import com.maxwell.tutm.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonEvents {
    private static final double VOID_Y = -10;

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || player.level().isClientSide()) return;

        if (event.getSource().is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (player.level().dimension() == Level.END) {
                int playerAccel = TimeManager.getCurrentMode() == PlayerTimeMode.ACCELERATING ? 2 : 1;
                int bossAccel = BossTimeManager.getAccelFactor();
                int totalAccel = Math.max(playerAccel, bossAccel);

                if (totalAccel >= 10) {
                    event.setCanceled(true);
                    moveToTimeRealm(player);
                }
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
            player.setInvulnerable(true);
            TimeRealmGenerator.generateArena(destination, new BlockPos(0, 60, 0));
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
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.level.isClientSide) return;
        ServerLevel level = (ServerLevel) event.level;
        if (!level.dimension().equals(ServerLevel.END)) return;
        AABB box = new AABB(
                -3.0E7, -64, -3.0E7,
                3.0E7, VOID_Y, 3.0E7
        );
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
            {
                if (item.getPersistentData().getBoolean("tutm_processed")) continue;
                if (item.getY() <= VOID_Y) {
                    ItemStack stack = item.getItem();
                    ItemStack result = ItemStack.EMPTY;
                    if (stack.is(Items.SAND)) {
                        result = new ItemStack(ModItems.TIME_SAND_DUST.get(), stack.getCount());
                    } else if (stack.is(ModItems.TIME_SAND_DUST.get())) {
                        result = new ItemStack(ModItems.TIME_SAND.get(), stack.getCount());
                    }
                    if (!result.isEmpty()) {
                        item.getPersistentData().putBoolean("tutm_processed", true);
                        item.discard();
                        ItemEntity newItem = new ItemEntity(
                                level,
                                item.getX(),
                                VOID_Y + 15,
                                item.getZ(),
                                result
                        );
                        newItem.setDeltaMovement(0, 2, 0);
                        level.addFreshEntity(newItem);
                    }
                }
            }
        }
    }
}