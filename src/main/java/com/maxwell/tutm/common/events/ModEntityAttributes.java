package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import com.maxwell.tutm.init.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.get(The_Ultimate_TimeManagerEntity.class),
                The_Ultimate_TimeManagerEntity.createAttributes().build()
        );
    }
}