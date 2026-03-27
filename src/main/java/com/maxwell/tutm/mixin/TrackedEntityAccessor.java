package com.maxwell.tutm.mixin;

import net.minecraft.server.level.ServerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.server.level.ChunkMap$TrackedEntity")
public interface TrackedEntityAccessor {
    // 実際にパケット送信を担当する ServerEntity を取得
    @Accessor("serverEntity")
    ServerEntity getServerEntity();
}