package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.config.ModConfig;
import com.maxwell.tutm.common.logic.sacrifice.TimeHaloState;
import com.maxwell.tutm.common.util.TimeHaloTooltipClient;
import com.maxwell.tutm.common.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.UUID;

public class TimeHaloItem extends Item implements ICurioItem {
    public TimeHaloItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        TooltipHelper.addNumberedLines(pTooltipComponents, "tooltip.time_halo.lore_base");
        if (pLevel != null && pLevel.isClientSide()) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                    TimeHaloTooltipClient.appendClientTooltip(pTooltipComponents)
            );
        }
    }
    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (!pLevel.isClientSide && pEntity instanceof Player player) {
            TimeHaloState state = TimeHaloState.get(pLevel);
            if (state == null) return;
            UUID currentOwner = state.getOwner();
            if (currentOwner == null) {
                state.setOwner(player.getUUID());
                return;
            }
            if (!ModConfig.ALLOW_MULTIPLE_HALOS.get() && !currentOwner.equals(player.getUUID())) {
                pStack.setCount(0);
                player.sendSystemMessage(Component.translatable("item.tutm.time_halo.desc.cantheld"));
            }
        }
    }
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        Level level = slotContext.entity().level();
        if (!level.isClientSide) {
            if (ModConfig.ALLOW_MULTIPLE_HALOS.get()) return true;
            TimeHaloState state = TimeHaloState.get(level);
            if (state != null && state.getOwner() != null) {
                return state.getOwner().equals(slotContext.entity().getUUID());
            }
        }
        return true;
    }

    @Override
    public ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source, int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }
}