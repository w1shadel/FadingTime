package com.maxwell.tutm.mixin;

import com.maxwell.tutm.common.entity.The_Ultimate_TimeManagerEntity;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class AbsoluteClientTimeMixin {

    @Inject(method = "runTick(Z)V", at = @At("HEAD"))
    private void tutm$onAbsoluteClientTick(boolean pRenderLevel, CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;

        if (mc.level != null && !mc.isPaused()) {
            long currentRealTime = Util.getMillis();
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (entity instanceof The_Ultimate_TimeManagerEntity boss) {
                    boss.absoluteRealTimeTick(currentRealTime);
                }
            }
        }
    }
}