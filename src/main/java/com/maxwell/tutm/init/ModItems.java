package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
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
    public static final RegistryObject<Item> CHRONO_CLOCK = ITEMS.register("chrono_clock",
            () -> new TimeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TIME_SAND = ITEMS.register("time_sand",
            () -> new ComponentItem(new Item.Properties().rarity(Rarity.UNCOMMON), Component.translatable("item.tutm.time_sand.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
    public static final RegistryObject<Item> TIME_SAND_DUST = ITEMS.register("time_sand_dust",
            () -> new ComponentItem(new Item.Properties(), Component.translatable("item.tutm.time_sand_dust.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
    public static final RegistryObject<Item> INFINITE_TIME_CLOCK = ITEMS.register("infinite_time_clock",
            () -> new ComponentItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), Component.translatable("item.tutm.infinite_time_clock.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
}
