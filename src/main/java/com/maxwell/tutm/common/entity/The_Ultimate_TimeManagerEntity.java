package com.maxwell.tutm.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class The_Ultimate_TimeManagerEntity extends Monster {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState attackAlphaAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> IS_SECOND_FORM = SynchedEntityData.defineId(The_Ultimate_TimeManagerEntity.class, EntityDataSerializers.BOOLEAN);

    public The_Ultimate_TimeManagerEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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
            // Server side logic for phase transition
            if (!this.isSecondForm() && this.getHealth() <= this.getMaxHealth() * 0.5F) {
                this.setSecondForm(true);
            }
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D;
    }
}