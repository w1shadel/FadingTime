package com.maxwell.tutm.common.items;

import com.maxwell.tutm.common.logic.damage.MemoryCorruptionLogic;
import io.github.kosianodangoo.trialmonolith.common.helper.EntityHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class TimeEraserItem extends Item {
    public TimeEraserItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .rarity(Rarity.EPIC)
                .fireResistant());
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 viewVec = player.getLookAngle();
            double reach = 5.0;
            Vec3 reachVec = eyePos.add(viewVec.scale(reach));
            AABB searchArea = player.getBoundingBox().inflate(reach);
            List<Entity> entities = level.getEntities(player, searchArea);
            LivingEntity target = null;
            for (Entity entity : entities) {
                AABB targetBB = entity.getBoundingBox().inflate(0.3);
                if (targetBB.clip(eyePos, reachVec).isPresent()) {
                    if (entity instanceof LivingEntity living) {
                        target = living;
                        break;
                    }
                }
            }
            if (target != null) {
                MemoryCorruptionLogic.nukeEntityMemory(target);
                target.setRemoved(Entity.RemovalReason.DISCARDED);
                if (!target.level().isClientSide) {
                    target.onRemovedFromWorld();
                }
                player.displayClientMessage(Component.literal("§d[断罪] §f空間干渉により対象の存在を削りました"), true);
                ((ServerLevel) level).sendParticles(ParticleTypes.FLASH, target.getX(), target.getEyeY(), target.getZ(), 1, 0, 0, 0, 0);
            }
            player.getCooldowns().addCooldown(this, 5);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable net.minecraft.world.level.Level level, List<Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        tooltip.add(Component.literal("§7右クリック: 対象の存在を物理的に抹消する"));
        tooltip.add(Component.literal("§8ASM / 防具 / 無敵時間を全てバイパス"));
    }
}