package com.maxwell.tutm.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityTickList.class)
public interface EntityTickListAccessor {
    @Invoker("remove")
    void callRemove(Entity entity);
}