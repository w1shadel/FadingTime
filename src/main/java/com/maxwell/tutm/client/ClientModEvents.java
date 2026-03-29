package com.maxwell.tutm.client;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.client.gui.TimeGaugeOverlay;
import com.maxwell.tutm.client.renderer.TimeDimensionEffects;
import com.maxwell.tutm.client.tutm_entity.The_Ultimate_Time_ManagerModel;
import com.maxwell.tutm.client.tutm_entity.Time_HaloItemModel;
import com.maxwell.tutm.init.ModEntities;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    public static final KeyMapping SELECT_KEY = new KeyMapping("key.tutm.select", GLFW.GLFW_KEY_V, "key.categories.tutm");
    public static final KeyMapping EXECUTE_KEY = new KeyMapping("key.tutm.execute", GLFW.GLFW_KEY_G, "key.categories.tutm");

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        ModEntities.RENDERER_MAP.forEach((reg, rendererClass) -> {
            event.registerEntityRenderer((EntityType) reg.get(), context -> {
                try {
                    return (EntityRenderer) rendererClass.getConstructor(EntityRendererProvider.Context.class).newInstance(context);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(The_Ultimate_Time_ManagerModel.LAYER_LOCATION, The_Ultimate_Time_ManagerModel::createBodyLayer);
        event.registerLayerDefinition(Time_HaloItemModel.LAYER_LOCATION, Time_HaloItemModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("time_gauge", TimeGaugeOverlay.HUD);
        event.registerAboveAll("time_ability_hud", com.maxwell.tutm.client.gui.TimeAbilityHUD.HUD);
    }

    @SubscribeEvent
    public static void onRegisterDimensionEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(new ResourceLocation(TUTM.MODID, "time_effects"), new TimeDimensionEffects());
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SELECT_KEY);
        event.register(EXECUTE_KEY);
    }

}