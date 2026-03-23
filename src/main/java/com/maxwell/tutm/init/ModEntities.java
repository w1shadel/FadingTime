package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TUTM.MODID);

    public static final RegistryObject<EntityType<The_Ultimate_TimeManagerEntity>> THE_ULTIMATE_TIME_MANAGER =
            ENTITIES.register("the_ultimate_time_manager",
                    () -> EntityType.Builder.of(The_Ultimate_TimeManagerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 2.2F)
                            .build("the_ultimate_time_manager"));

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier.Builder attributes = Monster.createMonsterAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 1000.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.4D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 20.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 10.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.FOLLOW_RANGE, 64.0D);

        event.put(THE_ULTIMATE_TIME_MANAGER.get(), attributes.build());
    }
}