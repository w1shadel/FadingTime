package com.maxwell.tutm.common.logic.sacrifice;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class HaloPartHelper {
    public static final String NBT_PARTS_BITMASK = "PartsCollected";

    // 時計にフェーズを記録する
    public static void collectPart(ItemStack clock, int phase) {
        CompoundTag tag = clock.getOrCreateTag();
        int mask = tag.getInt(NBT_PARTS_BITMASK);
        tag.putInt(NBT_PARTS_BITMASK, mask | (1 << phase));
    }

    // すでに記録されているか確認
    public static boolean hasPart(ItemStack clock, int phase) {
        if (!clock.hasTag()) return false;
        return (clock.getTag().getInt(NBT_PARTS_BITMASK) & (1 << phase)) != 0;
    }

    // 記録されたパーツの総数
    public static int getCollectedCount(ItemStack clock) {
        if (!clock.hasTag()) return 0;
        return Integer.bitCount(clock.getTag().getInt(NBT_PARTS_BITMASK));
    }
}