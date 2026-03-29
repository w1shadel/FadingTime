package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> TIME = ResourceKey.create(
            Registries.DAMAGE_TYPE, TUTM.getResourceLocation("time"));
    public static final ResourceKey<DamageType> LASER = ResourceKey.create(Registries.DAMAGE_TYPE, TUTM.getResourceLocation("laser"));

    public static DamageSource getTimeDamageSource(Level level, @Nullable Entity attacker) {
        Holder.Reference<DamageType> holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(TIME);
        if (attacker != null) {
            return new DamageSource(holder, attacker);
        }

        return new DamageSource(holder);
    }
    public static DamageSource getLaserDamageSource(Level level, Entity attacker) {
        var holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(LASER);
        return new DamageSource(holder, attacker);
    }
}