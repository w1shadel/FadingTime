package com.maxwell.tutm.mixin;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PostChain.class)
public interface PostChainAccessor {
    // PostChainのプライベートな passes フィールドを公開するゲッターを定義
    @Accessor("passes")
    List<PostPass> getPasses();
}