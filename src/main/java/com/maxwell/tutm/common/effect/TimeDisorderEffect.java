package com.maxwell.tutm.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * 時間障害効果: 時間に関する操作を一切使用できなくする
 * TimeManager側でこのエフェクトの有無をチェックして操作をブロックする
 */
public class TimeDisorderEffect extends MobEffect {
    public TimeDisorderEffect() {
        super(MobEffectCategory.HARMFUL, 0x8800CC); // 紫色
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // ティックごとの追加効果は不要（TimeManagerでブロックする）
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
