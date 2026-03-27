package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class TimeStopDebuggerItem extends Item {
    public TimeStopDebuggerItem() {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (player.isShiftKeyDown()) {
                TimeManager.requestStop(player);
            } else if (player.isSprinting() || player.getDeltaMovement().horizontalDistanceSqr() > 0.01) {
                TimeManager.requestRewind(player);
            } else {
                int current = TimeManager.getAccelerationFactor(player);
                int next = (current == 1) ? 2 : (current == 2) ? 5 : (current == 5) ? 10 : 1;
                TimeManager.setAccelerationFactor(player, next);
            }
            player.getCooldowns().addCooldown(this, 10);
        }
        return InteractionResultHolder.consume(itemstack);
    }
}