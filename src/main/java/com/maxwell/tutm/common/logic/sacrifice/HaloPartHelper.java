package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.common.items.HaloPartItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class HaloPartHelper {
    public static final String NBT_PARTS_BITMASK = "PartsCollected";

    public static void collectPart(ItemStack clock, int phase) {
        CompoundTag tag = clock.getOrCreateTag();
        int mask = tag.getInt(NBT_PARTS_BITMASK);
        tag.putInt(NBT_PARTS_BITMASK, mask | (1 << phase));
    }

    public static boolean hasPart(ItemStack clock, int phase) {
        return (clock.getOrCreateTag().getInt(NBT_PARTS_BITMASK) & (1 << phase)) != 0;
    }

    public static int getCollectedCount(ItemStack clock) {
        return Integer.bitCount(clock.getOrCreateTag().getInt(NBT_PARTS_BITMASK));
    }

    private static boolean isPartWithPhase(ItemStack stack, int phase) {
        if (stack.getItem() instanceof HaloPartItem) {
            return stack.getOrCreateTag().getInt("Phase") == phase;
        }
        return false;
    }

    public static boolean hasAlreadyCollected(Player player, int phase) {
        for (ItemStack stack : player.getInventory().items) {
            if (isPartWithPhase(stack, phase)) return true;
        }
        if (isPartWithPhase(player.getOffhandItem(), phase)) return true;
        return false;
    }
}