package com.maxwell.tutm.common.entity.ai;

import com.maxwell.tutm.common.entity.TemporalLaserEntity;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class LaserOctaBurstGoal extends Goal {
    private final The_Ultimate_TimeManagerEntity boss;
    private int timer = 0;
    private int placedCount = 0;

    public LaserOctaBurstGoal(The_Ultimate_TimeManagerEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        return boss.getTarget() != null && boss.tickCount % 200 == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return boss.getTarget() != null && placedCount < 8;
    }

    @Override
    public void start() {
        timer = 0;
        placedCount = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = boss.getTarget();
        if (target == null) return;
        timer++;
        if (timer % 2 == 0 && placedCount < 8) {
            double angle = placedCount * (Math.PI * 2 / 8);
            double x = target.getX() + Math.cos(angle) * 6;
            double z = target.getZ() + Math.sin(angle) * 6;
            Vec3 spawnPos = new Vec3(x, target.getY() + 2, z);
            int initialAge = -((7 - placedCount) * 2);
            TemporalLaserEntity laser = new TemporalLaserEntity(boss.level(), boss, target.position(), initialAge);
            boss.level().addFreshEntity(laser);
            placedCount++;
        }
    }

    @Override
    public void stop() {
        placedCount = 0;
        timer = 0;
    }
}