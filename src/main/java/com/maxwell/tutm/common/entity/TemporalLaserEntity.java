package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.client.renderer.TemporalLaserRenderer;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModEntities;
import com.maxwell.tutm.init.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
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
        renderer = TemporalLaserRenderer.class
)
public class TemporalLaserEntity extends Entity {
    public static final int CHARGE_TIME = 18;
    public static final int DURATION = 38;
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Vector3f> LASER_DIR = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.VECTOR3);
    private static final EntityDataAccessor<Boolean> STACKED = SynchedEntityData.defineId(TemporalLaserEntity.class, EntityDataSerializers.BOOLEAN);
    private Entity owner;

    public TemporalLaserEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public TemporalLaserEntity(Level level, LivingEntity owner, Vec3 targetPos, int inithalAge) {
        this(ModEntities.get(TemporalLaserEntity.class), level);
        this.owner = owner;
        this.entityData.set(AGE, inithalAge);
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

    @Override
    protected void defineSynchedData() {
        this.entityData.define(AGE, 0);
        this.entityData.define(LASER_DIR, new Vector3f(0, 0, 0));
        this.entityData.define(STACKED, false);
    }

    public int getLaserAge() {
        return this.entityData.get(AGE);
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
        int age = getLaserAge();
        if (this.entityData.get(STACKED)) {
            this.entityData.set(STACKED, false);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_BURST.get(), SoundSource.HOSTILE, 0.8F, 1.5F);
        }
        if (age == 1) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_CHARGE.get(), SoundSource.HOSTILE, 0.5F, 2.0F);
        }
        if (age == CHARGE_TIME) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.LASER_BURST.get(), SoundSource.HOSTILE, 0.8F, 1.5F);
        }
        this.entityData.set(AGE, age + 1);
        if (age >= CHARGE_TIME && age < DURATION) {
            if (!this.level().isClientSide) {
                applyDamageTrace();
            }
        }
        if (age >= DURATION) {
            this.discard();
        }
    }

    private void applyDamageTrace() {
        Vec3 start = this.position();
        Vec3 dir = getLaserDirection();
        java.util.Set<LivingEntity> hitInThisTick = new java.util.HashSet<>();
        for (int i = 0; i < 64; i++) {
            Vec3 checkPos = start.add(dir.scale(i));
            AABB area = new AABB(
                    checkPos.x - 0.5, checkPos.y - 0.8, checkPos.z - 0.5,
                    checkPos.x + 0.5, checkPos.y + 0.8, checkPos.z + 0.5
            );
            List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
            for (LivingEntity target : targets) {
                if (hitInThisTick.contains(target)) continue;
                if (!(this.owner instanceof The_Ultimate_TimeManagerEntity && target == this.owner)) {
                    EntityHelper.applyAbsoluteTimeAttack(target, this.owner, 10.0F);
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