package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.client.renderer.The_Ultimate_Time_ManagerRenderer;
import com.maxwell.tutm.common.entity.ai.ChronosGearGoal;
import com.maxwell.tutm.common.entity.ai.LaserOctaBurstGoal;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.common.network.UpdateBossBarPacket;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AutoRegisterEntity(
        name = "the_ultimate_time_manager",
        width = 0.6f, height = 2.2f,
        renderer = The_Ultimate_Time_ManagerRenderer.class
)
public class The_Ultimate_TimeManagerEntity extends Monster {
    private static final EntityDataAccessor<Boolean> IS_SECOND_FORM = SynchedEntityData.defineId(The_Ultimate_TimeManagerEntity.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAlphaAnimationState = new AnimationState();
    private final Set<UUID> trackingPlayers = new HashSet<>();
    private float lastHealth;
    private boolean lastSecondFormState;
    private int constantLaserTimer = 0;
    private double orbitAngle = 0;
    private int teleportCooldown = 0;
    private int divineWaveCooldown = 0;
    private int timeStopCooldown = 200;

    public The_Ultimate_TimeManagerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.lastHealth = -1.0F;
        this.lastSecondFormState = false;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 128.0D)
                .add(Attributes.FLYING_SPEED, 0.6D);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide) {
            super.aiStep();
            return;
        }
        float healthPercent = this.getHealth() / this.getMaxHealth();
        int factor = 1;
        if (healthPercent <= 0.25F) factor = 4;
        else if (healthPercent <= 0.50F) factor = 3;
        else if (healthPercent <= 0.75F) factor = 2;
        TimeManager.setBossAcceleration(factor);
        timeStopCooldown--;
        if (!TimeManager.isTimeStopped() && timeStopCooldown <= 0 && this.getTarget() != null) {
            if (this.random.nextFloat() < 0.25F) {
                TimeManager.startBossTimeStop((ServerLevel) this.level(), 60);
                timeStopCooldown = 400;
            }
        }
        TimeManager.startBossTimeStop((ServerLevel) this.level(), 120);
        if (this.getTarget() == null || !this.getTarget().isAlive()) {
            Player nearestPlayer = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 64.0D, false);
            if (nearestPlayer != null && !nearestPlayer.isCreative() && !nearestPlayer.isSpectator()) {
                this.setTarget(nearestPlayer);
            }
        }
        this.setNoGravity(true);
        LivingEntity target = this.getTarget();
        if (target != null) {
            handleFlightMovement(target);
            constantLaserTimer++;
            int fireRate = isSecondForm() ? 4 : 8;
            if (constantLaserTimer >= fireRate) {
                shootConstantLaser(target);
                constantLaserTimer = 0;
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5, 0.5, 0.5).add(0, 0.02, 0));
        }
        if (!this.level().isClientSide) {
            divineWaveCooldown--;
            if (divineWaveCooldown <= 0) {
                DivineWaveEntity wave = new DivineWaveEntity(this.level(), this);
                this.level().addFreshEntity(wave);
                divineWaveCooldown = isSecondForm() ? 400 : 600;
            }
        }
        super.aiStep();
    }

    @Override
    public Component getDisplayName() {
        if (isSecondForm()) {
            return Component.literal("The Ultimate Fading time Manager");
        }
        return Component.literal("The Ultimate Time Manager");
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_SECOND_FORM, false);
    }

    public boolean isSecondForm() {
        return this.entityData.get(IS_SECOND_FORM);
    }

    public void setSecondForm(boolean secondForm) {
        this.entityData.set(IS_SECOND_FORM, secondForm);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.idleAnimationState.animateWhen(!this.walkAnimationState.isStarted(), this.tickCount);
            if (this.isMoving()) {
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                this.walkAnimationState.stop();
            }
        } else {
            boolean currentForm = this.isSecondForm();
            if (!currentForm && this.getHealth() <= this.getMaxHealth() * 0.5F) {
                this.setSecondForm(true);
                currentForm = true;
            }
            if (this.getHealth() != this.lastHealth || currentForm != this.lastSecondFormState) {
                this.lastHealth = this.getHealth();
                this.lastSecondFormState = currentForm;
                this.broadcastBossBarPacket(true);
            }
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        if (!this.level().isClientSide) {
            this.trackingPlayers.add(player.getUUID());
            sendBossBarPacket(player, true);
        }
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        if (!this.level().isClientSide) {
            this.trackingPlayers.remove(player.getUUID());
            sendBossBarPacket(player, false);
        }
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            broadcastBossBarPacket(false);
        }
    }

    private void sendBossBarPacket(ServerPlayer player, boolean shouldDisplay) {
        UpdateBossBarPacket packet;
        if (shouldDisplay) {
            packet = new UpdateBossBarPacket(true, this.getHealth(), this.getMaxHealth(), this.getDisplayName(), this.isSecondForm());
        } else {
            packet = new UpdateBossBarPacket(false, 0, 1, null, false);
        }
        TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    private void broadcastBossBarPacket(boolean shouldDisplay) {
        if (this.level().isClientSide) return;
        UpdateBossBarPacket packet;
        if (shouldDisplay) {
            packet = new UpdateBossBarPacket(true, this.getHealth(), this.getMaxHealth(), this.getDisplayName(), this.isSecondForm());
        } else {
            packet = new UpdateBossBarPacket(false, 0, 1, null, false);
        }
        for (UUID uuid : this.trackingPlayers) {
            ServerPlayer player = (ServerPlayer) this.level().getPlayerByUUID(uuid);
            if (player != null) {
                TUTMPacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
            }
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new LaserOctaBurstGoal(this));
        this.goalSelector.addGoal(2, new ChronosGearGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    private void handleFlightMovement(LivingEntity target) {
        orbitAngle += isSecondForm() ? 0.08 : 0.04;
        double radius = 8.0 + Math.sin(this.tickCount * 0.05) * 4.0;
        double targetX = target.getX() + Math.cos(orbitAngle) * radius;
        double targetZ = target.getZ() + Math.sin(orbitAngle) * radius;
        double targetY = target.getY() + 6.0 + Math.sin(this.tickCount * 0.1) * 2.0;
        Vec3 targetVec = new Vec3(targetX, targetY, targetZ);
        Vec3 moveVec = targetVec.subtract(this.position());
        double speed = isSecondForm() ? 0.25 : 0.15;
        this.setDeltaMovement(this.getDeltaMovement().add(moveVec.normalize().scale(speed)));
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.8, 0.8));
        this.getLookControl().setLookAt(target, 100F, 100F);
        double dist = this.distanceTo(target);
        teleportCooldown--;
        if (teleportCooldown <= 0) {
            if (dist > 32.0) {
                this.moveTo(target.getX(), target.getY() + 10, target.getZ());
                teleportCooldown = 100;
            } else if (dist < 5.0) {
                Vec3 behind = target.getLookAngle().reverse().scale(10);
                this.moveTo(target.getX() + behind.x, target.getY() + 5, target.getZ() + behind.z);
                teleportCooldown = 60;
            }
        }
    }

    private void shootConstantLaser(LivingEntity target) {
        Vec3 velocity = target.getDeltaMovement();
        Vec3 predictedPos = target.position().add(
                velocity.x * 15,
                0,
                velocity.z * 15
        ).add((random.nextDouble() - 0.5) * 3, 0, (random.nextDouble() - 0.5) * 3);
        TemporalLaserEntity laser = new TemporalLaserEntity(this.level(), this, predictedPos, 0);
        this.level().addFreshEntity(laser);
    }
}