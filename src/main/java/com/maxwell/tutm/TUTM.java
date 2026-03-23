package com.maxwell.tutm;

import com.maxwell.tutm.common.network.TUTMPacketHandler;
import com.maxwell.tutm.init.ModEntities;
import com.maxwell.tutm.init.ModItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TUTM.MODID)
public class TUTM
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "tutm";
    public TUTM() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItem.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        TUTMPacketHandler.register();

        MinecraftForge.EVENT_BUS.register(this);
    }
}
