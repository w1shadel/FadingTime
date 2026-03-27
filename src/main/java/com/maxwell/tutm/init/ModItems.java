package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.ChronoTankItem;
import com.maxwell.tutm.common.items.TemporalShooterItem;
import com.maxwell.tutm.common.items.TimeStopDebuggerItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, TUTM.MODID);
    public static final RegistryObject<Item> DEBUG = ITEMS.register("debug",
            TimeStopDebuggerItem::new);
    public static final RegistryObject<Item> TEMPORAL_SHOOTER = ITEMS.register("temporal_shooter",
            () -> new TemporalShooterItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHRONO_TANK1 = ITEMS.register("chrono_tank1",
            () -> new ChronoTankItem(1, Rarity.COMMON));
    public static final RegistryObject<Item> CHRONO_TANK2 = ITEMS.register("chrono_tank2",
            () -> new ChronoTankItem(2, Rarity.COMMON));
    public static final RegistryObject<Item> CHRONO_TANK3 = ITEMS.register("chrono_tank3",
            () -> new ChronoTankItem(3, Rarity.COMMON));
}
