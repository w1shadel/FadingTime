package com.maxwell.tutm.init;

import com.maxwell.tutm.TUTM;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TUTM.MODID);
    public static final RegistryObject<CreativeModeTab> TUTM_TAB = CREATIVE_MODE_TABS.register("tutm_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.LUNAR_CHRONO_CLOCK.get()))
                    .title(Component.translatable("creativetab.tutm_tab"))
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(item -> {
                            output.accept(item.get());
                        });
                    })
                    .build());
}