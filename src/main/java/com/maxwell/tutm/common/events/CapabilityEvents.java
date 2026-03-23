package com.maxwell.tutm.common.events;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.capability.TimeDataCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class CapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(TimeDataCapability.Provider.IDENTIFIER, new TimeDataCapability.Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(TimeDataCapability.INSTANCE).ifPresent(oldData -> {
                event.getEntity().getCapability(TimeDataCapability.INSTANCE).ifPresent(newData -> {
                    newData.copyFrom(oldData);
                });
            });
        }
    }
}