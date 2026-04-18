package com.maxwell.tutm.mixin;

import com.maxwell.tutm.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    @Final
    private ServerLevel level;
    @Shadow
    @Final
    private Set<UUID> heroesOfTheVillage;

    @Shadow
    public abstract boolean isVictory();

    @Inject(
            method = {"tick"},
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/entity/raid/Raid;status:Lnet/minecraft/world/entity/raid/Raid$RaidStatus;",
                    opcode = 181,
                    shift = At.Shift.AFTER
            )
    )
    private void tutm$onStatusChanged(CallbackInfo ci) {
        if (this.isVictory() && !this.level.isDay()) {
            for (UUID uuid : this.heroesOfTheVillage) {
                Entity entity = this.level.getEntity(uuid);
                if (entity instanceof ServerPlayer player && !player.isSpectator()) {
                    ItemStack reward = new ItemStack(ModItems.NIGHT_VICTORY_EMBLEM.get());
                    if (!player.getInventory().add(reward)) {
                        player.drop(reward, false);
                    }
                    player.sendSystemMessage(Component.translatable("chat.tutm.message.2"));
                    this.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        }
    }
}