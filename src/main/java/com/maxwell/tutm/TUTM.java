package com.maxwell.tutm;

import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.init.ModEffects;
import com.maxwell.tutm.init.ModEntities;
import com.maxwell.tutm.init.ModItems;
import com.maxwell.tutm.init.ModSounds;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("removal")
@Mod(TUTM.MODID)
public class TUTM {
    public static final String MODID = "tutm";
    private static final Logger LOGGER = LogManager.getLogger();

    public TUTM() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modEventBus);
        ModEntities.autoRegister();
        ModEntities.ENTITIES.register(modEventBus);
        TUTMPacketHandler.register();
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEffects.MOB_EFFECTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation getResourceLocation(String location) {
        return getResourceLocation(MODID, location);
    }

    public static ResourceLocation getResourceLocation(String nameSpace, String location) {
        return new ResourceLocation(nameSpace, location);
    }
}
