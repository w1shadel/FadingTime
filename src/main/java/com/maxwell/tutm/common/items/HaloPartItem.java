package com.maxwell.tutm.common.items;

import com.maxwell.tutm.init.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HaloPartItem extends Item {
    public HaloPartItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack createPart(int phase) {
        ItemStack stack = new ItemStack(ModItems.HALO_PART.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("Phase", phase);
        return stack;
    }

    public static int getPhaseFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("Phase")) {
            return tag.getInt("Phase");
        }
        return -1; 
    }
}