package com.maxwell.tutm.common.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

@SuppressWarnings("removal")
public class TUTMDimensions {
    public static final ResourceKey<Level> TIME_REALM_LEVEL_KEY = ResourceKey.create(
            Registries.DIMENSION,
            new ResourceLocation("tutm", "time_realm")
    );
    public static final ResourceKey<DimensionType> TIME_REALM_TYPE_KEY = ResourceKey.create(
            Registries.DIMENSION_TYPE,
            new ResourceLocation("tutm", "time_realm")
    );
}