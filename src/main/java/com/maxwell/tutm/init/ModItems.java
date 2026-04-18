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
    public static final RegistryObject<Item> TIME_HALO = ITEMS.register("time_halo",
            () -> new TimeHaloItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHRONO_TANK1 = ITEMS.register("chrono_tank1",
            () -> new ChronoTankItem(1, Rarity.COMMON));
    public static final RegistryObject<Item> CHRONO_TANK2 = ITEMS.register("chrono_tank2",
            () -> new ChronoTankItem(2, Rarity.COMMON));
    public static final RegistryObject<Item> CHRONO_TANK3 = ITEMS.register("chrono_tank3",
            () -> new ChronoTankItem(3, Rarity.COMMON));
    public static final RegistryObject<Item> CHRONO_CLOCK = ITEMS.register("chrono_clock",
            () -> new TimeItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HALO_PART = ITEMS.register("halo_part",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_CAT_CORPSE = ITEMS.register("cat_soul",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(1)));
    public static final RegistryObject<Item> SLIME_ETHEREAL_AMALGAM = ITEMS.register("slime_ethereal_amalgam",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NIGHT_VICTORY_EMBLEM = ITEMS.register("night_victory_emblem",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> FUSED_SOUL = ITEMS.register("fused_soul",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> FAKE_HALO = ITEMS.register("fake_halo",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ABSOLUTE_CHRONOS_CLOCK = ITEMS.register("absolute_chronos_clock",
            () -> new LunarTimeComponentItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant(), Component.translatable("item.tutm.absolute_chronos_clock.desc")));
    public static final RegistryObject<Item> LUNAR_CHRONO_CLOCK = ITEMS.register("lunar_chrono_clock",
            () -> new LunaChronosItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final RegistryObject<Item> LUNAR_CHRONO_CLOCK_CHARGED = ITEMS.register("lunar_chrono_clock_charged",
            () -> new LunarTimeComponentItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON).fireResistant(), Component.translatable("item.tutm.lunar_chrono_clock_charge.desc")));
    public static final RegistryObject<Item> LUNAR_CHRONO_CLOCK_NETHER = ITEMS.register("lunar_chrono_clock_nether",
            () -> new LunarTimeComponentItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE), Component.translatable("item.tutm.lunar_chrono_clock_nether.desc")));
    public static final RegistryObject<Item> LUNAR_CHRONO_CLOCK_END = ITEMS.register("lunar_chrono_clock_end",
            () -> new LunarTimeComponentItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), Component.translatable("item.tutm.lunar_chrono_clock_end.desc")));
    public static final RegistryObject<Item> LUNAR_CHRONO_CLOCK_TIME = ITEMS.register("lunar_chrono_clock_time",
            () -> new LastClockItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC), Component.translatable("item.tutm.lunar_chrono_clock_time.desc")));
    public static final RegistryObject<Item> TIME_SAND = ITEMS.register("time_sand",
            () -> new ComponentItem(new Item.Properties().rarity(Rarity.UNCOMMON), Component.translatable("item.tutm.time_sand.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
    public static final RegistryObject<Item> TIME_SAND_DUST = ITEMS.register("time_sand_dust",
            () -> new ComponentItem(new Item.Properties(), Component.translatable("item.tutm.time_sand_dust.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
    public static final RegistryObject<Item> INFINITE_TIME_CLOCK = ITEMS.register("infinite_time_clock",
            () -> new ComponentItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC), Component.translatable("item.tutm.infinite_time_clock.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
    public static final RegistryObject<Item> CHRONOSGODS_TIME_FRAME = ITEMS.register("chronos_gods_time_frame",
            () -> new ComponentItem(new Item.Properties().rarity(Rarity.EPIC), Component.translatable("item.tutm.chronos_gods_time_frame.desc").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)));
}
