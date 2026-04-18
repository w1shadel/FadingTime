package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, TUTM.MODID);
    public static final RegistryObject<Attribute> TIME_RECOVERY_RATE = ATTRIBUTES.register("time_recovery_rate",
            () -> new RangedAttribute("attribute.name.tutm.time_recovery_rate", 5000.0, 0.0, Double.MAX_VALUE).setSyncable(true));
    public static final RegistryObject<Attribute> MAX_TIME_COST = ATTRIBUTES.register("max_time_cost",
            () -> new RangedAttribute("attribute.name.tutm.max_time_cost", 1000.0, 0.0, Double.MAX_VALUE).setSyncable(true));
}