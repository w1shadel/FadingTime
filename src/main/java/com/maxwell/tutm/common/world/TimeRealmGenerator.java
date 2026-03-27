package com.maxwell.tutm.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class TimeRealmGenerator {
    public static void generateArena(ServerLevel level, BlockPos center) {
        int radius = 50;
        RandomSource random = level.getRandom();
        BlockState[] palette = {
                Blocks.OBSIDIAN.defaultBlockState(),
                Blocks.CRYING_OBSIDIAN.defaultBlockState(),
                Blocks.END_STONE.defaultBlockState(),
                Blocks.BLACKSTONE.defaultBlockState(),
                Blocks.GLOWSTONE.defaultBlockState(),
                Blocks.GILDED_BLACKSTONE.defaultBlockState()
        };
        BlockPos monolithPos = center.offset(50, 0, 50);
        generateMonolith(level, monolithPos);
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z <= radius * radius) {
                    mPos.set(center.getX() + x, center.getY(), center.getZ() + z);
                    BlockState state = palette[random.nextInt(palette.length)];
                    level.setBlock(mPos, state, 2);
                    level.setBlock(mPos.below(), Blocks.BEDROCK.defaultBlockState(), 2);
                    double dist = Math.sqrt(x * x + z * z);
                    if (dist > radius - 1) {
                        level.setBlock(mPos.above(), Blocks.YELLOW_STAINED_GLASS.defaultBlockState(), 2);
                    }
                }
            }
        }
        for (int i = -radius; i <= radius; i++) {
            level.setBlock(center.offset(i, 0, 0), Blocks.GOLD_BLOCK.defaultBlockState(), 2);
            level.setBlock(center.offset(0, 0, i), Blocks.GOLD_BLOCK.defaultBlockState(), 2);
        }
    }

    public static void generateMonolith(ServerLevel level, BlockPos basePos) {
        RandomSource random = level.getRandom();
        int height = 15 + random.nextInt(10);
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++) {
            int offsetX = y / 4;
            int offsetZ = y / 4;
            int thickness = (height - y) / 4 + 1;
            for (int dx = -thickness; dx <= thickness; dx++) {
                for (int dz = -thickness; dz <= thickness; dz++) {
                    if (random.nextFloat() > 0.2f) {
                        mPos.set(basePos.getX() + offsetX + dx, basePos.getY() + y, basePos.getZ() + offsetZ + dz);
                        BlockState state = Blocks.OBSIDIAN.defaultBlockState();
                        float rand = random.nextFloat();
                        if (rand > 0.7f) state = Blocks.CRYING_OBSIDIAN.defaultBlockState();
                        else if (rand > 0.5f) state = Blocks.GILDED_BLACKSTONE.defaultBlockState();
                        level.setBlock(mPos, state, 2);
                    }
                }
            }
        }
        BlockPos topPos = basePos.offset(height / 4, height, height / 4);
        level.setBlock(topPos, Blocks.SHROOMLIGHT.defaultBlockState(), 2);
    }
}