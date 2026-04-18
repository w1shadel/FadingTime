package com.maxwell.tutm.common.logic.dont_use;

import com.maxwell.tutm.mixin.EntityTickListAccessor;
import com.maxwell.tutm.mixin.ServerLevelAccessor;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Ultrakill {
    private static Unsafe unsafe;
    private static long deadOffset;
    private static long removalReasonOffset;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
            Field deadField = ObfuscationReflectionHelper.findField(LivingEntity.class, "f_20890_");
            deadOffset = unsafe.objectFieldOffset(deadField);
            Field removalField = ObfuscationReflectionHelper.findField(Entity.class, "f_146795_");
            removalReasonOffset = unsafe.objectFieldOffset(removalField);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void kill(Entity target) {
        if (target == null) return;
        unsafe.putBoolean(target, deadOffset, true);
        unsafe.putObject(target, removalReasonOffset, Entity.RemovalReason.DISCARDED);
        if (target.level() instanceof ServerLevel serverLevel) {
            try {
                EntityTickList tickList = ((ServerLevelAccessor) serverLevel).getEntityTickList();
                ((EntityTickListAccessor) tickList).callRemove(target);

            } catch (Exception e) {
                target.onRemovedFromWorld();
                e.printStackTrace();
            }
            target.setRemoved(Entity.RemovalReason.DISCARDED);
            serverLevel.getChunkSource().broadcast(target, new ClientboundRemoveEntitiesPacket(target.getId()));
        }
    }
}