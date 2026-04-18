package com.maxwell.tutm.client.screen;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientTimeHandler {
    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null && !mc.isPaused()) {
                long currentRealTime = Util.getMillis();
                for (Entity entity : mc.level.entitiesForRendering()) {
                    if (entity instanceof The_Ultimate_TimeManagerEntity boss) {
                        boss.absoluteRealTimeTick(currentRealTime);
                    }
                }
            }
        }
    }
}