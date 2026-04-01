package com.maxwell.tutm;

import com.maxwell.tutm.client.renderer.TimeHaloCurioRenderer;
import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@SuppressWarnings("removal")
@Mod(TUTM.MODID)
public class TUTM {
    public static final String MODID = "tutm";
    public static final Logger LOGGER = LogManager.getLogger();

    public TUTM() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModEntities.autoRegister();
        ModEntities.ENTITIES.register(modEventBus);
        TUTMPacketHandler.register();
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModLoadingContext.get().registerConfig(Type.COMMON, ModConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::clientSetup);
    }

    public static ResourceLocation getResourceLocation(String location) {
        return getResourceLocation(MODID, location);
    }

    public static ResourceLocation getResourceLocation(String nameSpace, String location) {
        return new ResourceLocation(nameSpace, location);
    }
    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            CuriosRendererRegistry.register(
                    ModItems.TIME_HALO.get(),
                    TimeHaloCurioRenderer::new
            );
            LOGGER.info("TUTM: Registered Curio Renderer for TIME_HALO");
        });
    }
}
