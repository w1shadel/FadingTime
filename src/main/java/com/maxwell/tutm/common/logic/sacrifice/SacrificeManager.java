package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.init.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SacrificeManager {

    public static EntityType<?> getTargetForPhase(int phase) {
        return switch (phase) {
            case 0 -> EntityType.ZOMBIE;
            case 1 -> EntityType.SKELETON;
            case 2 -> EntityType.SPIDER;
            case 3 -> EntityType.CREEPER;
            case 4 -> EntityType.WITCH;
            case 5 -> EntityType.ENDERMAN;
            case 6 -> EntityType.PHANTOM;
            case 7 -> EntityType.WITHER_SKELETON;
            default -> EntityType.PIG;
        };
    }

    public static String getHintForPhase(int phase) {
        return switch (phase) {
            case 0 -> "tooltip.tutm.hint.zombie";
            case 1 -> "tooltip.tutm.hint.skeleton";
            case 2 -> "tooltip.tutm.hint.spider";
            case 3 -> "tooltip.tutm.hint.creeper";
            case 4 -> "tooltip.tutm.hint.witch";
            case 5 -> "tooltip.tutm.hint.enderman";
            case 6 -> "tooltip.tutm.hint.phantom";
            case 7 -> "tooltip.tutm.hint.wither_skeleton";
            default -> "tooltip.tutm.hint.default";
        };
    }

    public static void dropPart(Level level, Vec3 pos) {
        ItemEntity item = new ItemEntity(level, pos.x, pos.y, pos.z,new ItemStack(ModItems.HALO_PART.get()));
        level.addFreshEntity(item);
    }

    public static boolean isCorrectSacrifice(LivingEntity entity, Level level) {
        int phase = level.getMoonPhase();
        return entity.getType() == getTargetForPhase(phase);
    }

}