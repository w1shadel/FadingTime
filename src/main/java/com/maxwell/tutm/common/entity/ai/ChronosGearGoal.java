package com.maxwell.tutm.common.entity.ai;

import com.maxwell.tutm.common.entity.ChronosGearEntity;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * クロノス・ギアゴール
 * 第一形態: 300tick(15秒)に1回 → 3個召喚
 * 第二形態: 200tick(10秒)に1回 → 5個召喚
 */
public class ChronosGearGoal extends Goal {
    private final The_Ultimate_TimeManagerEntity boss;

    public ChronosGearGoal(The_Ultimate_TimeManagerEntity boss) {
        this.boss = boss;
        this.setFlags(EnumSet.noneOf(Flag.class));
    }

    @Override
    public boolean canUse() {
        int interval = boss.isSecondForm() ? 200 : 300;
        return boss.getTarget() != null && boss.tickCount % interval == 0 && boss.tickCount > 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        int gearCount = boss.isSecondForm() ? 5 : 3;
        for (int i = 0; i < gearCount; i++) {
            double angle = i * (Math.PI * 2.0 / gearCount);
            double radius = 4.0;
            double spawnX = boss.getX() + Math.cos(angle) * radius;
            double spawnY = boss.getY() + 1.5;
            double spawnZ = boss.getZ() + Math.sin(angle) * radius;
            ChronosGearEntity gear = new ChronosGearEntity(boss.level(), boss, new Vec3(spawnX, spawnY, spawnZ));
            boss.level().addFreshEntity(gear);
        }
    }
}
