package com.maxwell.tutm.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(PersistentEntitySectionManager.class)
public interface EntityManagerAccessor {
    // sectionStorage (f_157489_) を取得
    @Accessor("sectionStorage")
    EntitySectionStorage<Entity> getSectionStorage();
}