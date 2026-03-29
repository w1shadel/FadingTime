package com.maxwell.tutm.common.logic.sacrifice;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.common.items.LunaChronosItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TUTM.MODID)
public class HaloSacrificeEvent {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Level level = event.getEntity().level();
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) return;

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack clock = player.getMainHandItem().getItem() instanceof LunaChronosItem ? player.getMainHandItem() :
                    (player.getOffhandItem().getItem() instanceof LunaChronosItem ? player.getOffhandItem() : ItemStack.EMPTY);

            if (!clock.isEmpty()) {
                if (SacrificeManager.isCorrectSacrifice(event.getEntity(), level)) {
                    ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
                    int phase = (overworld != null) ? overworld.getMoonPhase() : level.getMoonPhase();
                    if (!HaloPartHelper.hasPart(clock, phase)) {
                        HaloPartHelper.collectPart(clock, phase);
                        player.displayClientMessage(Component.literal("§6時計に新たなパーツが刻み込まれた..."), true);
                        serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT,
                                event.getEntity().getX(), event.getEntity().getY() + 1.0, event.getEntity().getZ(),
                                20, 0.5, 0.5, 0.5, 0.1);
                        player.playSound(SoundEvents.ZOMBIE_VILLAGER_CONVERTED, 1.0f, 1.2f);
                        SacrificeManager.dropPart(level, event.getEntity().position());
                    }
                }
            }
        }
    }
}