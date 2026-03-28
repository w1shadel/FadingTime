package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.client.renderer.DivineWaveRenderer;
import com.maxwell.tutm.common.util.AutoRegisterEntity;
import com.maxwell.tutm.init.ModEffects;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AutoRegisterEntity(
        name = "divine_wave",
        width = 1.0f,
        height = 1.0f,
        category = MobCategory.MISC,
        renderer = DivineWaveRenderer.class
)
public class DivineWaveEntity extends Entity {
    public static final int MAX_RADIUS = 60;
    public static final int EXPAND_TICKS = 60;
    public static final int FADE_TICKS = 10;
    private static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(DivineWaveEntity.class, EntityDataSerializers.INT);
    private final Set<UUID> alreadyHit = new HashSet<>();
    private Entity owner;

    public DivineWaveEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public DivineWaveEntity(Level level, Entity owner) {
        this(ModEntities.get(DivineWaveEntity.class), level);
        this.owner = owner;
        this.setPos(owner.getX(), owner.getY(), owner.getZ());
        this.entityData.set(AGE, 0);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(AGE, 0);
    }

    public int getWaveAge() {
        return this.entityData.get(AGE);
    }

    public float getCurrentRadius() {
        int age = getWaveAge();
        if (age >= EXPAND_TICKS) return MAX_RADIUS;
        return (float) age / EXPAND_TICKS * MAX_RADIUS;
    }

    @Override
    public void tick() {
        super.tick();
        int age = getWaveAge();
        this.entityData.set(AGE, age + 1);
        if (age >= EXPAND_TICKS + FADE_TICKS) {
            this.discard();
            return;
        }
        if (this.level().isClientSide) return;
        float radius = getCurrentRadius();
        if (radius <= 0) return;
        float innerR = Math.max(0, radius - 3.0f);
        Vec3 center = this.position();
        AABB broad = new AABB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
        );
        List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, broad);
        for (LivingEntity t : targets) {
            if (t == owner || t instanceof The_Ultimate_TimeManagerEntity) continue;
            if (t instanceof Player p && (p.isCreative() || p.isSpectator())) continue;
            if (alreadyHit.contains(t.getUUID())) continue;
            double dist = t.position().distanceTo(center);
            if (dist <= radius && dist >= innerR) {
                applyWaveEffect(t);
                alreadyHit.add(t.getUUID());
            }
        }
    }

    private void applyWaveEffect(LivingEntity target) {
        int duration = 200 + this.level().random.nextInt(141);
        target.addEffect(new MobEffectInstance(MobEffects.POISON, duration, 0, false, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.WITHER, duration, 0, false, true, true));
        target.addEffect(new MobEffectInstance(ModEffects.TIME_DISORDER.get(), duration, 0, false, true, true));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        this.entityData.set(AGE, nbt.getInt("WaveAge"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        nbt.putInt("WaveAge", getWaveAge());
    }
}
