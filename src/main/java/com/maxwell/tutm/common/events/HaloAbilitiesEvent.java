package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.entity.ChronosGearEntity;
import com.maxwell.tutm.common.entity.TemporalLaserEntity;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.common.items.TimeHaloItem;
import com.maxwell.tutm.common.util.CurioUtil;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModDamageTypes;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HaloAbilitiesEvent {
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("fa233e7c-4180-4865-b01b-2cee95b3f75e");
    private static final UUID ARMOR_MODIFIER_ID = UUID.fromString("7f3d3b7c-5c3a-4a69-8e43-fce75a4a582c");
    private static final ThreadLocal<Boolean> IS_PROCESSING_HALO_ATTACK = ThreadLocal.withInitial(() -> false);
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player) || player.level().isClientSide) return;
        AttributeInstance attackDamageAttribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance armorAttribute = player.getAttribute(Attributes.ARMOR);

        if (CurioUtil.hasHalo(player)) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            entity.setLastHurtByMob(null);
            entity.removeAllEffects();

            entity.fallDistance = 0;
            player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                double finalAtkBonus = data.attackBonus;
                double finalDefBonus = data.defenseBonus;
                double atkMultiplier = Math.max(0.01, (100.0 + finalAtkBonus) / 100.0);
                AttributeModifier oldAttackModifier = attackDamageAttribute.getModifier(ATTACK_DAMAGE_MODIFIER_ID);
                if (oldAttackModifier == null || Math.abs(oldAttackModifier.getAmount() - (atkMultiplier - 1.0)) > 0.01) {
                    if (oldAttackModifier != null) attackDamageAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER_ID);
                    attackDamageAttribute.addTransientModifier(new AttributeModifier(
                            ATTACK_DAMAGE_MODIFIER_ID, "Halo Attack Bonus", atkMultiplier - 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL
                    ));
                }
                AttributeModifier oldArmorModifier = armorAttribute.getModifier(ARMOR_MODIFIER_ID);
                if (oldArmorModifier == null || Math.abs(oldArmorModifier.getAmount() - finalDefBonus) > 0.1) {
                    if (oldArmorModifier != null) armorAttribute.removeModifier(ARMOR_MODIFIER_ID);
                    armorAttribute.addTransientModifier(new AttributeModifier(
                            ARMOR_MODIFIER_ID, "Halo Armor Bonus", finalDefBonus, AttributeModifier.Operation.ADDITION
                    ));
                }
            });

        } else {
            if (!player.isCreative() && !player.isSpectator() && player.getAbilities().mayfly) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }

            if (attackDamageAttribute.getModifier(ATTACK_DAMAGE_MODIFIER_ID) != null) {
                attackDamageAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER_ID);
            }
            if (armorAttribute.getModifier(ARMOR_MODIFIER_ID) != null) {
                armorAttribute.removeModifier(ARMOR_MODIFIER_ID);
            }
        }
    }
    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target instanceof Player player && CurioUtil.hasHalo(player)) {
            event.setAmount(event.getAmount() * 0.8F);
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                EntityHelper.applyDomainRetribution(attacker, player, event.getAmount());
            }
            ItemStack haloStack = CurioUtil.getHaloStack(player);
            if (!haloStack.isEmpty() && !player.getCooldowns().isOnCooldown(haloStack.getItem())) {
                if (player.getHealth() - event.getAmount() <= 2.0F) {
                    event.setCanceled(true);
                    player.setHealth(player.getMaxHealth());
                    player.getFoodData().setFoodLevel(20);
                    int ticks = ModConfig.COOLDOWNTIMER.get() * 20;
                    player.getCooldowns().addCooldown(haloStack.getItem(), ticks);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerAttack(LivingDamageEvent event) {
        if (event.getSource().getDirectEntity() instanceof TemporalLaserEntity ||
                event.getSource().getDirectEntity() instanceof ChronosGearEntity) return;
        if (event.getSource().is(ModDamageTypes.LASER)) return;
        if (IS_PROCESSING_HALO_ATTACK.get()) return;
        if (event.getSource().getEntity() instanceof ServerPlayer player && CurioUtil.hasHalo(player)) {
            LivingEntity target = event.getEntity();
            IS_PROCESSING_HALO_ATTACK.set(true);
            try {
                player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                    float baseDamage = event.getAmount();
                    float atkMultiplier = 1.0F + (float) (data.attackBonus / 100.0);
                    baseDamage *= atkMultiplier;
                    for (int i = 0; i < 10; i++) {
                        EntityHelper.applyAbsoluteTimeAttack(target, player, baseDamage);
                    }
                    for (int i = 0; i < 8; i++) {
                        double phi = Math.random() * Math.PI * 2;
                        double theta = Math.acos(2 * Math.random() - 1);
                        double r = 10.0;
                        Vec3 spawnPos = target.position().add(
                                r * Math.sin(theta) * Math.cos(phi),
                                r * Math.sin(theta) * Math.sin(phi),
                                r * Math.cos(theta)
                        );
                        TemporalLaserEntity laser = new TemporalLaserEntity(player.level(), player, spawnPos, target.position(), 0);
                        player.level().addFreshEntity(laser);
                    }
                    float healthRatio = player.getHealth() / player.getMaxHealth();
                    int gearCount = (healthRatio <= 0.5F) ? 5 : 3;
                    for (int i = 0; i < gearCount; i++) {
                        double angle = (Math.PI * 2 / gearCount) * i;
                        double radius = 1.5;
                        Vec3 gearSpawnPos = player.position().add(
                                Math.cos(angle) * radius,
                                1.2,
                                Math.sin(angle) * radius
                        );
                        ChronosGearEntity gear = new ChronosGearEntity(player.level(), player, gearSpawnPos);
                        gear.setTarget(target);
                        player.level().addFreshEntity(gear);
                    }
                });
            } finally {
                IS_PROCESSING_HALO_ATTACK.set(false);
            }
        }
    }
    @SubscribeEvent
    public static void onSetTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player player && CurioUtil.hasHalo(player)) {
            if (event.getEntity() instanceof The_Ultimate_TimeManagerEntity) {
                return;
            }
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player player) {
            Iterator<ItemEntity> iter = event.getDrops().iterator();
            while (iter.hasNext()) {
                ItemEntity drop = iter.next();
                ItemStack stack = drop.getItem();

                if (stack.getItem() instanceof TimeHaloItem) {
                    iter.remove();
                    player.getInventory().add(stack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
                ItemStack stack = oldPlayer.getInventory().getItem(i);
                if (stack.getItem() instanceof TimeHaloItem) {
                    newPlayer.getInventory().add(stack.copy());
                }
            }
        }
    }
}