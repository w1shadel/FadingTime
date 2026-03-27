package com.maxwell.tutm.common.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class ChronoTankItem extends Item implements ICurioItem {
    private final int tier;

    public ChronoTankItem(int tier, Rarity rarity) {
        super(new Item.Properties().stacksTo(1).rarity(rarity));
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}