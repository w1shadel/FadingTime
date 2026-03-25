package com.maxwell.tutm.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(EntityTickList.class)
public interface EntityTickListAccessor {
    // 内部の remove メソッドを呼び出す
    @Invoker("remove")
    void callRemove(Entity entity);
}