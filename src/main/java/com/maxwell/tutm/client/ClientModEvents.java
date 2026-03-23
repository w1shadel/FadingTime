package com.maxwell.tutm.client;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.gui.TimeGaugeOverlay;
import com.maxwell.tutm.client.renderer.The_Ultimate_Time_ManagerRenderer;
import com.maxwell.tutm.client.tutm_entity.The_Ultimate_Time_ManagerModel;
import com.maxwell.tutm.init.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.THE_ULTIMATE_TIME_MANAGER.get(), The_Ultimate_Time_ManagerRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(The_Ultimate_Time_ManagerModel.LAYER_LOCATION, The_Ultimate_Time_ManagerModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("time_gauge", TimeGaugeOverlay.HUD);
    }

}