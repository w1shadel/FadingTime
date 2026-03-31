package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModDamageTypes;
import com.maxwell.tutm.init.ModEntities;
import com.maxwell.tutm.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

@AutoRegisterEntity(
        name = "temporal_laser",
        width = 0.5f, height = 0.5f,
        renderer = "com.maxwell.tutm.client.renderer.TemporalLaserRenderer"
)
public class TemporalLaserEntity extends Entity {
    public static int getChargeTime() { return ModConfig.TEMPORAL_LASER_CHARGE_TIME.get(); }
    public static int getDuration() { return ModConfig.TEMPORAL_LASER_DURATION.get(); }
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> LASER_DIR = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> STACKED = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SWEEP_MODE = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> RADIUS = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.FLOAT);
    private Entity owner;
    private static final EntityDataAccessor<Integer> TRACKING_TARGET_ID = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.INT);
    public TemporalLaserEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public TemporalLaserEntity(Level level, LivingEntity owner, Vec3 targetPos, int initialAge) {
        this(ModEntities.get(TemporalLaserEntity.class), level);
        this.owner = owner;
        this.entityData.set(AGE, initialAge);
        this.entityData.set(RADIUS, 1.0F); // 初期値
        Vec3 pos = owner.position().add(0, owner.getEyeHeight(), 0);
        this.setPos(pos.x, pos.y, pos.z);
        this.xo = pos.x;
        this.yo = pos.y;
        this.zo = pos.z;
        double dx = targetPos.x - this.getX();
        double dy = targetPos.y - this.getY();
        double dz = targetPos.z - this.getZ();
        double dXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90F;
        float pitch = (float) -(Mth.atan2(dy, dXZ) * (180D / Math.PI));
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
        this.entityData.set(LASER_DIR, new Vec3(dx, dy, dz).normalize().toVector3f());
        if (TimeManager.isTimeStopped()) {
            this.entityData.set(STACKED, true);
        }
    }
    public TemporalLaserEntity(Level level, LivingEntity owner, Vec3 spawnPos, Vec3 targetPos, int initialAge) {
        this(ModEntities.get(TemporalLaserEntity.class), level);
        this.owner = owner;
        this.entityData.set(AGE, initialAge);
        this.entityData.set(RADIUS, 1.0F); // 初期値
        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        this.xo = spawnPos.x;
        this.yo = spawnPos.y;
        this.zo = spawnPos.z;
        double dx = targetPos.x - this.getX();
        double dy = targetPos.y - this.getY();
        double dz = targetPos.z - this.getZ();
        double dXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90F;
        float pitch = (float) -(Mth.atan2(dy, dXZ) * (180D / Math.PI));
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
        this.entityData.set(LASER_DIR, new Vec3(dx, dy, dz).normalize().toVector3f());
        if (TimeManager.isTimeStopped()) {
            this.entityData.set(STACKED, true);
        }
    }
    @Override
    protected void defineSynchedData() {
        this.entityData.define(AGE, 0);
        this.entityData.define(LASER_DIR, new Vector3f(0, 0, 0));
        this.entityData.define(STACKED, false);
        this.entityData.define(TRACKING_TARGET_ID, -1);
        this.entityData.define(IS_SWEEP_MODE, false);
        this.entityData.define(RADIUS, 1.0F);
    }

    public int getLaserAge() {
        return this.entityData.get(AGE);
    }
    public void setTrackingTarget(Entity target) {
        if (target != null) {
            this.entityData.set(TRACKING_TARGET_ID, target.getId());
        }
    }
    public void setRadius(float radius) {
        this.entityData.set(RADIUS, radius);
    }
    public float getRadius() {
        return this.entityData.get(RADIUS);
    }
    public Entity getOwner() {
        return this.owner;
    }
    public Vec3 getLaserDirection() {
        Vector3f v = this.entityData.get(LASER_DIR);
        return new Vec3(v.x(), v.y(), v.z());
    }

    @Override
    public void tick() {
        if (TimeManager.isTimeStopped()) {
            this.entityData.set(STACKED, true);
            this.entityData.set(AGE, 0);
            this.setPos(this.xo, this.yo, this.zo);
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }
        super.tick();
        if (!this.level().isClientSide) {
            if (this.owner == null || !this.owner.isAlive() || this.owner.isRemoved()) {
                this.discard();
                return;
            }
        }
        int age = getLaserAge();

        if (!this.level().isClientSide && age < (getChargeTime() - 2)) {
            int targetId = this.entityData.get(TRACKING_TARGET_ID);
            if (targetId != -1) {
                Entity target = this.level().getEntity(targetId);
                if (target != null) {
                    Vec3 targetPos = target.getBoundingBox().getCenter();
                    Vec3 dir = targetPos.subtract(this.position()).normalize();
                    this.entityData.set(LASER_DIR, dir.toVector3f());
                    double dx = targetPos.x - this.getX();
                    double dy = targetPos.y - this.getY();
                    double dz = targetPos.z - this.getZ();
                    double dXZ = Math.sqrt(dx * dx + dz * dz);
                    this.setYRot((float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90F);
                    this.setXRot((float) -(Mth.atan2(dy, dXZ) * (180D / Math.PI)));
                }
            }
        }
        if (this.entityData.get(STACKED)) {
            this.entityData.set(STACKED, false);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_BURST.get(), SoundSource.HOSTILE, 0.8F, 1.5F);
        }
        if (age == 1) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_CHARGE.get(), SoundSource.HOSTILE, 0.5F, 2.0F);
        }
        if (age == getChargeTime()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_BURST.get(), SoundSource.HOSTILE, 0.8F, 1.5F);
        }
        this.entityData.set(AGE, age + 1);
        
        int maxDuration = getDuration();
        
        if (age >= getChargeTime() && age < maxDuration) {
            if (!this.level().isClientSide) {
                applyDamageTrace();
            }
        }
        if (age >= maxDuration) {
            this.discard();
        }
    }

    private void applyDamageTrace() {
        Vec3 start = this.position();
        Vec3 dir = getLaserDirection();
        java.util.Set<LivingEntity> hitInThisTick = new java.util.HashSet<>();
        int range = ModConfig.TEMPORAL_LASER_RANGE.get().intValue();
        float radius = getRadius();
        for (int i = 0; i < range; i += (int)Math.max(1, radius)) {
            Vec3 checkPos = start.add(dir.scale(i));
            AABB area = new AABB(
                    checkPos.x - radius, checkPos.y - radius, checkPos.z - radius,
                    checkPos.x + radius, checkPos.y + radius, checkPos.z + radius
            );
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            for (LivingEntity target : targets) {
                if (hitInThisTick.contains(target)) continue;
                if (!(this.owner instanceof The_Ultimate_TimeManagerEntity && target == this.owner)) {
                    DamageSource laserSource = ModDamageTypes.getLaserDamageSource(this.level(), this.owner, this);
                    EntityHelper.applyAbsoluteTimeAttack(target, this.owner, ModConfig.TEMPORAL_LASER_DAMAGE.get().floatValue(), laserSource);
                    hitInThisTick.add(target);
                }
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        if (nbt.contains("LaserAge")) this.entityData.set(AGE, nbt.getInt("LaserAge"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("LaserAge", getLaserAge());
    }
}