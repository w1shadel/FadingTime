package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.HaloPartItem;
import com.maxwell.tutm.common.items.LunaChronosItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class HaloSacrificeEvent {
    @SubscribeEvent
    public static void onEntityItemPickup(net.minecraftforge.event.entity.player.EntityItemPickupEvent event) {
        ItemStack pickedUpStack = event.getItem().getItem();
        if (pickedUpStack.getItem() instanceof HaloPartItem) {
            ItemStack clock = event.getEntity().getOffhandItem();
            if (clock.getItem() instanceof LunaChronosItem) {
                int phase = HaloPartItem.getPhaseFromStack(pickedUpStack);
                if (phase != -1 && !HaloPartHelper.hasPart(clock, phase)) {
                    HaloPartHelper.collectPart(clock, phase);
                    event.getEntity().displayClientMessage(Component.literal("§6時計に新たなパーツが刻み込まれた..."), true);
                    event.getItem().discard();
                    event.setResult(Event.Result.ALLOW);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            Level level = player.level();
            ItemStack clock = player.getMainHandItem().getItem() instanceof LunaChronosItem ? player.getMainHandItem() : player.getOffhandItem();
            if (clock.getItem() instanceof LunaChronosItem) {
                if (SacrificeManager.isCorrectSacrifice(event.getEntity(), level)) {
                    int phase = level.getMoonPhase();
                    if (!HaloPartHelper.hasPart(clock, phase)) {
                        SacrificeManager.dropPart(level, event.getEntity().position(), phase);
                    }
                }
            }
        }
    }
}