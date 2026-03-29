package com.maxwell.tutm.mixin;

import com.maxwell.tutm.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class RaidMixin {

    @Shadow @Final private ServerLevel level;

    @Inject(
            method = {"tick"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void tutm$onAddHeroEffect(CallbackInfo ci, int i, boolean flag3, int k, Iterator<UUID> var5, UUID uuid) {
        // 夜間判定
        if (!this.level.isDay()) {
            net.minecraft.world.entity.Entity entity = this.level.getEntity(uuid);

            // プレイヤーかつ、まだ死んでいない場合に報酬を付与
            if (entity instanceof ServerPlayer player && !player.isSpectator()) {
                ItemStack reward = new ItemStack(ModItems.NIGHT_VICTORY_EMBLEM.get());

                if (!player.getInventory().add(reward)) {
                    player.drop(reward, false);
                }

                // 演出
                player.sendSystemMessage(Component.literal("§5月影の下、襲撃を退けし証を授かろう..."));
                this.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        }
    }
}