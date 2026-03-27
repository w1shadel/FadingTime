package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.entity.TemporalLaserEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TemporalShooterItem extends Item {
    public TemporalShooterItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int count) {
        if (!level.isClientSide && entity instanceof Player player) {
            int duration = this.getUseDuration(stack) - count;
            if (duration % 10 == 0) {
                Vec3 eyePos = player.getEyePosition();
                Vec3 lookVec = player.getLookAngle();
                Vec3 targetPos = eyePos.add(lookVec.scale(64.0));
                TemporalLaserEntity laser = new TemporalLaserEntity(level, player, targetPos,0);
                level.addFreshEntity(laser);
                player.swing(player.getUsedItemHand());
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }
}