package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.client.renderer.ChronosGearRenderer;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@AutoRegisterEntity(
        name = "chronos_gear",
        width = 1.2f,
        height = 1.2f,
        category = MobCategory.MISC,
        renderer =  "com.maxwell.tutm.client.renderer.ChronosGearRenderer"
)
public class ChronosGearEntity extends Entity {
    public static final int WAIT_TICKS = 40;
    public static final int BOUNCE_TICKS = 20;
    public static final int MAX_FLY_TICKS = 60;
    public static final int STATE_WAITING = 0;
    public static final int STATE_FLYING = 1;
    public static final int STATE_BOUNCING = 2;
    private static final EntityDataAccessor<Boolean> STACKED = SynchedEntityData.defineId(ChronosGearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int MAX_BOUNCES = 3;
    private int state = STATE_WAITING;
    private int stateTimer = 0;
    private Entity owner;
    private int bounceCount = 0;

    public ChronosGearEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public ChronosGearEntity(Level level, Entity owner, Vec3 spawnPos) {
        this(ModEntities.get(ChronosGearEntity.class), level);
        this.owner = owner;
        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        this.state = STATE_WAITING;
        this.stateTimer = 0;
        if (TimeManager.isTimeStopped()) {
            this.entityData.set(STACKED, true);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STACKED, false);
    }

    @Override
    public void tick() {
        if (TimeManager.isTimeStopped()) {
            this.entityData.set(STACKED, true);
            this.setPos(this.xo, this.yo, this.zo);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        super.tick();
        if (this.level().isClientSide) {
            Vec3 move = this.getDeltaMovement();
            this.setPos(this.getX() + move.x, this.getY() + move.y, this.getZ() + move.z);
        } else {
            if (this.entityData.get(STACKED)) {
                this.entityData.set(STACKED, false);
            }
            stateTimer++;
            switch (state) {
                case STATE_WAITING -> tickWaiting();
                case STATE_FLYING -> tickFlying();
                case STATE_BOUNCING -> tickBouncing();
            }
        }
    }

    private void tickWaiting() {
        this.setDeltaMovement(Vec3.ZERO);
        if (stateTimer >= WAIT_TICKS) {
            launchToTarget();
        }
    }

    private void tickFlying() {
        Vec3 move = this.getDeltaMovement();
        float targetYaw = (float) Math.toDegrees(Math.atan2(-move.x, move.z));
        float targetPitch = (float) Math.toDegrees(Math.asin(move.y / move.length()));
        this.setYRot(rotLerp(this.getYRot(), targetYaw, 20f));
        this.setXRot(rotLerp(this.getXRot(), targetPitch, 20f));
        this.move(MoverType.SELF, move);
        if (this.horizontalCollision || this.verticalCollision) {
            enterBounce();
            return;
        }
        checkEntityCollisions();
        if (stateTimer >= MAX_FLY_TICKS) {
            enterBounce();
        }
    }

    private void tickBouncing() {
        this.setDeltaMovement(Vec3.ZERO);
        if (stateTimer >= BOUNCE_TICKS) {
            bounceCount++;
            if (bounceCount >= MAX_BOUNCES) {
                this.discard();
            } else {
                launchToTarget();
            }
        }
    }

    private void launchToTarget() {
        LivingEntity target = findTarget();
        if (target == null) {
            this.discard();
            return;
        }
        Vec3 targetPos = target.position().add(0, target.getEyeHeight() / 2.0, 0);
        Vec3 dir = targetPos.subtract(this.position()).normalize();
        double yaw = Math.toDegrees(Math.atan2(-dir.x, dir.z));
        double pitch = Math.toDegrees(Math.asin(dir.y));
        this.setYRot((float) yaw);
        this.setXRot((float) pitch);
        double speed = 1.0;
        this.setDeltaMovement(dir.scale(speed));
        state = STATE_FLYING;
        stateTimer = 0;
    }

    private float rotLerp(float start, float end, float maxStep) {
        float f = Mth.wrapDegrees(end - start);
        if (f > maxStep) f = maxStep;
        if (f < -maxStep) f = -maxStep;
        return start + f;
    }

    private void checkEntityCollisions() {
        AABB hitBox = this.getBoundingBox().inflate(0.3);
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, hitBox);
        for (LivingEntity t : targets) {
            if (t == owner || t instanceof The_Ultimate_TimeManagerEntity) continue;
            if (t instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
            EntityHelper.applyAbsoluteTimeAttack(t, owner, 10f);
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        }
    }

    private void enterBounce() {
        this.setDeltaMovement(Vec3.ZERO);
        state = STATE_BOUNCING;
        stateTimer = 0;

    }

    private LivingEntity findTarget() {
        return this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 64, false);
    }

    public int getGearState() {
        return state;
    }

    public int getStateTimer() {
        return stateTimer;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.state = nbt.getInt("GearState");
        this.stateTimer = nbt.getInt("GearTimer");
        this.bounceCount = nbt.getInt("BounceCount");
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 1024.0D;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("GearState", this.state);
        nbt.putInt("GearTimer", this.stateTimer);
        nbt.putInt("BounceCount", this.bounceCount);
    }
}