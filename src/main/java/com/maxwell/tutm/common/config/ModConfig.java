package com.maxwell.tutm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Boss Settings
    public static final ForgeConfigSpec.DoubleValue BOSS_MAX_HEALTH;
    public static final ForgeConfigSpec.DoubleValue BOSS_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.DoubleValue BOSS_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.DoubleValue BOSS_FLYING_SPEED;
    public static final ForgeConfigSpec.DoubleValue BOSS_FOLLOW_RANGE;
    public static final ForgeConfigSpec.IntValue BOSS_TIME_STOP_COOLDOWN;
    public static final ForgeConfigSpec.IntValue BOSS_DIVINE_WAVE_COOLDOWN_P1;
    public static final ForgeConfigSpec.IntValue BOSS_DIVINE_WAVE_COOLDOWN_P2;

    // Chronos Gear Settings
    public static final ForgeConfigSpec.DoubleValue CHRONOS_GEAR_DAMAGE;
    public static final ForgeConfigSpec.IntValue CHRONOS_GEAR_MAX_BOUNCES;
    public static final ForgeConfigSpec.IntValue CHRONOS_GEAR_WAIT_TICKS;
    public static final ForgeConfigSpec.IntValue CHRONOS_GEAR_BOUNCE_TICKS;
    public static final ForgeConfigSpec.IntValue CHRONOS_GEAR_MAX_FLY_TICKS;
    public static final ForgeConfigSpec.DoubleValue CHRONOS_GEAR_SPEED;

    // Divine Wave Settings
    public static final ForgeConfigSpec.IntValue DIVINE_WAVE_MAX_RADIUS;
    public static final ForgeConfigSpec.IntValue DIVINE_WAVE_EXPAND_TICKS;
    public static final ForgeConfigSpec.IntValue DIVINE_WAVE_FADE_TICKS;
    public static final ForgeConfigSpec.IntValue DIVINE_WAVE_EFFECT_DURATION_MIN;
    public static final ForgeConfigSpec.IntValue DIVINE_WAVE_EFFECT_DURATION_MAX;

    // Temporal Laser Settings
    public static final ForgeConfigSpec.DoubleValue TEMPORAL_LASER_DAMAGE;
    public static final ForgeConfigSpec.IntValue TEMPORAL_LASER_CHARGE_TIME;
    public static final ForgeConfigSpec.IntValue TEMPORAL_LASER_DURATION;
    public static final ForgeConfigSpec.DoubleValue TEMPORAL_LASER_RANGE;

    // Skill Cost Settings
    public static final ForgeConfigSpec.DoubleValue COST_ACCELERATION;
    public static final ForgeConfigSpec.DoubleValue COST_TIME_STOP;
    public static final ForgeConfigSpec.DoubleValue COST_REWIND;

    public static final ForgeConfigSpec.IntValue COOLDOWNTIMER;
    public static final ForgeConfigSpec.BooleanValue ALLOW_MULTIPLE_HALOS;

    static {
        BUILDER.push("Boss Settings");
        BOSS_MAX_HEALTH = BUILDER.comment("The maximum health of The Ultimate Time Manager").defineInRange("bossMaxHealth", 1000.0, 1.0, Double.MAX_VALUE);
        BOSS_ATTACK_DAMAGE = BUILDER.comment("The base attack damage of The Ultimate Time Manager").defineInRange("bossAttackDamage", 20.0, 0.0, Double.MAX_VALUE);
        BOSS_MOVEMENT_SPEED = BUILDER.comment("The movement speed of The Ultimate Time Manager").defineInRange("bossMovementSpeed", 0.4, 0.0, 2.0);
        BOSS_FLYING_SPEED = BUILDER.comment("The flying speed of The Ultimate Time Manager").defineInRange("bossFlyingSpeed", 0.6, 0.0, 2.0);
        BOSS_FOLLOW_RANGE = BUILDER.comment("The follow range of The Ultimate Time Manager").defineInRange("bossFollowRange", 128.0, 1.0, 512.0);
        BOSS_TIME_STOP_COOLDOWN = BUILDER.comment("The cooldown for the boss's time stop ability (in ticks)").defineInRange("bossTimeStopCooldown", 600, 0, 72000);
        BOSS_DIVINE_WAVE_COOLDOWN_P1 = BUILDER.comment("The cooldown for the boss's divine wave ability in Phase 1 (in ticks)").defineInRange("bossDivineWaveCooldownPhase1", 600, 0, 72000);
        BOSS_DIVINE_WAVE_COOLDOWN_P2 = BUILDER.comment("The cooldown for the boss's divine wave ability in Phase 2 (in ticks)").defineInRange("bossDivineWaveCooldownPhase2", 400, 0, 72000);
        BUILDER.pop();

        BUILDER.push("Chronos Gear Settings");
        CHRONOS_GEAR_DAMAGE = BUILDER.comment("The damage dealt by Chronos Gear").defineInRange("chronosGearDamage", 10.0, 0.0, Double.MAX_VALUE);
        CHRONOS_GEAR_MAX_BOUNCES = BUILDER.comment("The maximum number of times Chronos Gear can bounce").defineInRange("chronosGearMaxBounces", 3, 0, 100);
        CHRONOS_GEAR_WAIT_TICKS = BUILDER.comment("The number of ticks Chronos Gear waits before launching").defineInRange("chronosGearWaitTicks", 40, 0, 1200);
        CHRONOS_GEAR_BOUNCE_TICKS = BUILDER.comment("The number of ticks Chronos Gear stays in the bounce state").defineInRange("chronosGearBounceTicks", 20, 0, 1200);
        CHRONOS_GEAR_MAX_FLY_TICKS = BUILDER.comment("The maximum number of ticks Chronos Gear can fly").defineInRange("chronosGearMaxFlyTicks", 60, 1, 1200);
        CHRONOS_GEAR_SPEED = BUILDER.comment("The flight speed of Chronos Gear").defineInRange("chronosGearSpeed", 1.0, 0.1, 10.0);
        BUILDER.pop();

        BUILDER.push("Divine Wave Settings");
        DIVINE_WAVE_MAX_RADIUS = BUILDER.comment("The maximum radius of the Divine Wave").defineInRange("divineWaveMaxRadius", 60, 1, 256);
        DIVINE_WAVE_EXPAND_TICKS = BUILDER.comment("The number of ticks it takes for the Divine Wave to expand").defineInRange("divineWaveExpandTicks", 60, 1, 600);
        DIVINE_WAVE_FADE_TICKS = BUILDER.comment("The number of ticks the Divine Wave takes to fade out").defineInRange("divineWaveFadeTicks", 10, 0, 600);
        DIVINE_WAVE_EFFECT_DURATION_MIN = BUILDER.comment("Minimum duration for effects applied by Divine Wave (in ticks)").defineInRange("divineWaveEffectDurationMin", 200, 0, 12000);
        DIVINE_WAVE_EFFECT_DURATION_MAX = BUILDER.comment("Maximum duration for effects applied by Divine Wave (in ticks)").defineInRange("divineWaveEffectDurationMax", 340, 0, 12000);
        BUILDER.pop();

        BUILDER.push("Temporal Laser Settings");
        TEMPORAL_LASER_DAMAGE = BUILDER.comment("The damage dealt by Temporal Laser").defineInRange("temporalLaserDamage", 10.0, 0.0, Double.MAX_VALUE);
        TEMPORAL_LASER_CHARGE_TIME = BUILDER.comment("The charge time of the Temporal Laser (in ticks)").defineInRange("temporalLaserChargeTime", 18, 1, 600);
        TEMPORAL_LASER_DURATION = BUILDER.comment("The total duration of the Temporal Laser (in ticks)").defineInRange("temporalLaserDuration", 38, 1, 1200);
        TEMPORAL_LASER_RANGE = BUILDER.comment("The maximum range of the Temporal Laser").defineInRange("temporalLaserRange", 64.0, 1.0, 512.0);
        BUILDER.pop();

        BUILDER.push("Skill Cost Settings");
        COST_ACCELERATION = BUILDER.comment("Base cost for Time Acceleration").defineInRange("costAcceleration", 5.0, 0.0, 1000.0);
        COST_TIME_STOP = BUILDER.comment("Cost for Time Stop").defineInRange("costTimeStop", 100.0, 0.0, 1000.0);
        COST_REWIND = BUILDER.comment("Cost for Rewinding Time").defineInRange("costRewind", 150.0, 0.0, 1000.0);
        BUILDER.pop();

        BUILDER.push("Halo Ability Settings");
        COOLDOWNTIMER = BUILDER.comment("Cooldown on Death Nullification(seconds)").defineInRange("cooldowntimer", 200, 0, Integer.MAX_VALUE);
        ALLOW_MULTIPLE_HALOS = BUILDER.comment("Whether multiple players can have the Time Halo simultaneously").define("allowMultipleHalos", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
