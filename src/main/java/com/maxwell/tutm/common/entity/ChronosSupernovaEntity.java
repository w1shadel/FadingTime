package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.common.util.EntityHelper;
import com.maxwell.tutm.init.ModDamageTypes;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@AutoRegisterEntity(
        name = "chronos_supernova",
        width = 2.0f, height = 2.0f,
        renderer = "com.maxwell.tutm.client.renderer.ChronosSupernovaRenderer"
)
public class ChronosSupernovaEntity extends Entity {
    private static final EntityDataAccessor<Integer> AGE = SynchedEntityData.defineId(ChronosSupernovaEntity.class, EntityDataSerializers.INT);
    public static final int CHARGE_TIME = 200; 
    public static final int EXPLOSION_TIME = 20;

    private LivingEntity owner;

    public ChronosSupernovaEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public ChronosSupernovaEntity(Level level, LivingEntity owner, double x, double y, double z) {
        this(ModEntities.get(ChronosSupernovaEntity.class), level);
        this.owner = owner;
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(AGE, 0);
    }

    public int getEntityAge() {
        return this.entityData.get(AGE);
    }

    @Override
    public void tick() {
        if (TimeManager.isTimeStopped()) return;
        super.tick();

        if (this.level().isClientSide) {

        } else {
            if (this.owner == null || !this.owner.isAlive() || this.owner.isRemoved()) {
                this.discard();
                return;
            }
        }
        
        int currentAge = getEntityAge();
        this.entityData.set(AGE, currentAge + 1);

        if (this.level().isClientSide) {
            if (currentAge < CHARGE_TIME) {

                for (int i = 0; i < 5; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double radius = 10.0 + Math.random() * 5.0;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    this.level().addParticle(ParticleTypes.ENCHANT,
                            this.getX() + offsetX, this.getY() + Math.random() * 5.0, this.getZ() + offsetZ,
                            -offsetX * 0.1, -1.0, -offsetZ * 0.1);
                }
            } else if (currentAge == CHARGE_TIME) {

                for (int i = 0; i < 200; i++) {
                    double speed = 2.0 + Math.random() * 4.0;
                    double theta = Math.random() * Math.PI * 2;
                    double phi = Math.acos(2 * Math.random() - 1);
                    double vx = Math.sin(phi) * Math.cos(theta) * speed;
                    double vy = Math.sin(phi) * Math.sin(theta) * speed;
                    double vz = Math.cos(phi) * speed;
                    this.level().addParticle(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), vx, vy, vz);
                    this.level().addParticle(ParticleTypes.EXPLOSION, this.getX() + vx*10, this.getY() + vy*10, this.getZ() + vz*10, 0, 0, 0);
                }
            }
        } else {
            if (currentAge == CHARGE_TIME) {
                detonate();
            }
            if (currentAge > CHARGE_TIME + EXPLOSION_TIME) {
                this.discard();
            }
        }
    }

    private void detonate() {
        double maxRadius = 64.0; 
        AABB area = new AABB(
                this.getX() - maxRadius, this.getY() - maxRadius, this.getZ() - maxRadius,
                this.getX() + maxRadius, this.getY() + maxRadius, this.getZ() + maxRadius
        );
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, area);
        for (LivingEntity target : targets) {
            if (target == this.owner) continue;
            double distance = this.distanceTo(target);
            if (distance <= maxRadius) {

                float damage = (float) (200.0 * (1.0 - (distance / maxRadius)));
                if (damage < 10.0f) damage = 10.0f;
                Entity attacker = this.owner != null ? this.owner : this;
                DamageSource source = ModDamageTypes.getLaserDamageSource(this.level(), attacker, this);

                if (target instanceof net.minecraft.world.entity.player.Player player && (player.isCreative() || player.isSpectator())) continue;

                target.hurt(source, damage);
                if (this.owner != null) {
                    EntityHelper.applyAbsoluteTimeAttack(target, this.owner, damage, source);
                }
            }
        }

        if (!this.level().isClientSide && this.owner != null) {
            int numLasers = 32;
            double goldenRatio = (1 + Math.sqrt(5.0)) / 2.0;
            double angleIncrement = Math.PI * 2 * goldenRatio;
            for (int i = 0; i < numLasers; i++) {
                double t = (double) i / numLasers;
                double inclination = Math.acos(1 - 2 * t);
                double azimuth = angleIncrement * i;
                
                double dx = Math.sin(inclination) * Math.cos(azimuth);
                double dy = Math.sin(inclination) * Math.sin(azimuth);
                double dz = Math.cos(inclination);
                
                Vec3 spawnPos = this.position().add(0, 1.5, 0); 
                Vec3 targetPos = spawnPos.add(dx * 10, dy * 10, dz * 10);
                
                TemporalLaserEntity laser = new TemporalLaserEntity(this.level(), (LivingEntity) this.owner, spawnPos, targetPos, 0);
                this.level().addFreshEntity(laser);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.entityData.set(AGE, nbt.getInt("Age"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("Age", this.entityData.get(AGE));
    }
}
