package com.maxwell.tutm.client;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, value = Dist.CLIENT)
public class ClientTemporalEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TimeRenderHandler.updateClientEffects();
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            TimeRenderHandler.updateClientEffects();
        }
    }
}