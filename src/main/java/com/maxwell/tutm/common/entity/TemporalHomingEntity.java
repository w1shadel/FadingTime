package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModDamageTypes;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

@AutoRegisterEntity(
        name = "temporal_homing_ball",
        width = 0.8f, height = 0.8f,
        renderer = "com.maxwell.tutm.client.renderer.TemporalHomingRenderer"
)
public class TemporalHomingEntity extends Entity implements ITUTMEntity {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(TemporalHomingEntity.class, EntityDataSerializers.INT);
    private LivingEntity owner;
    private LivingEntity target;
    private UUID targetUUID;

    public TemporalHomingEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public TemporalHomingEntity(Level level, LivingEntity owner, LivingEntity target) {
        this(ModEntities.get(TemporalHomingEntity.class), level);
        this.owner = owner;
        this.target = target;
        this.targetUUID = target.getUUID();
        this.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(AGE, 0);
    }

    @Override
    public void tick() {
        if (TimeManager.isTimeStopped()) return;
        super.tick();
        if (!this.level().isClientSide) {
            if (this.owner == null || !this.owner.isAlive() || this.owner.isRemoved()) {
                this.discard();
                return;
            }
            this.entityData.set(AGE, this.entityData.get(AGE) + 1);
            if (this.target == null || !this.target.isAlive()) {
                if (this.targetUUID != null) {
                    this.target = (LivingEntity) ((net.minecraft.server.level.ServerLevel) this.level()).getEntity(this.targetUUID);
                }
            }
            if (this.target != null) {
                Vec3 targetPos = this.target.getBoundingBox().getCenter();
                Vec3 dir = targetPos.subtract(this.position()).normalize();
                int currentAge = this.getBallAge();
                double speed = 0.4 + (currentAge * 0.015);
                if (speed > 1.5) speed = 1.5;
                this.setDeltaMovement(this.getDeltaMovement().lerp(dir.scale(speed), 0.25));
            }
            this.setPos(this.getX() + this.getDeltaMovement().x, this.getY() + this.getDeltaMovement().y, this.getZ() + this.getDeltaMovement().z);
            if (this.target != null && this.getBoundingBox().intersects(this.target.getBoundingBox())) {
                onHit(this.target);
            }
            if (this.entityData.get(AGE) > 200) {
                this.discard();
            }
        }
    }

    private void onHit(LivingEntity victim) {
        DamageSource source = ModDamageTypes.getLaserDamageSource(this.level(), this.owner, this);
        float damage = ModConfig.TEMPORAL_LASER_DAMAGE.get().floatValue() * 1.5f;
        EntityHelper.applyAbsoluteTimeAttack(victim, this.owner, damage, source);
        this.discard();
    }

    public int getBallAge() {
        return this.entityData.get(AGE);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.entityData.set(AGE, nbt.getInt("Age"));
        if (nbt.hasUUID("TargetUUID")) this.targetUUID = nbt.getUUID("TargetUUID");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Age", this.entityData.get(AGE));
        if (this.targetUUID != null) nbt.putUUID("TargetUUID", this.targetUUID);
    }
}
