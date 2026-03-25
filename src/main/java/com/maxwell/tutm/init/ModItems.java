package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.TimeEraserItem;
import com.maxwell.tutm.common.items.TimeStopDebuggerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TUTM.MODID);
    public static final RegistryObject<Item> DEBUG = ITEMS.register("debug",
            TimeStopDebuggerItem::new);
    public static final RegistryObject<Item> DEBUG2 = ITEMS.register("debug2",
            TimeEraserItem::new);
}
