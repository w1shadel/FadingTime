package com.maxwell.tutm.common.capability;

import com.maxwell.tutm.TUTM;
import com.maxwell.tutm.init.ModAttributes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("removal")
@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TimeDataCapability {
    public static final Capability<TimeData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(TimeData.class);
    }

    public static class TimeData {
        public int tier = 1;
        public double currentCost = 0;
        public int selectedSkill = 0;
        public double attackBonus = 0;
        public double defenseBonus = 0;

        // 属性から最大値を取得する（LivingEntityが必要）
        public double getMaxCost(LivingEntity entity) {
            AttributeInstance instance = entity.getAttribute(ModAttributes.MAX_TIME_COST.get());
            if (instance != null) {
                return instance.getValue();
            }
            return 1000.0; // フォールバック
        }
        public double getRecoveryRate(LivingEntity entity) {
            var instance = entity.getAttribute(ModAttributes.TIME_RECOVERY_RATE.get());
            return instance != null ? instance.getValue() : 5000.0; // フォールバック
        }

        public void updateBaseAttributes(LivingEntity entity) {
            var maxInst = entity.getAttribute(ModAttributes.MAX_TIME_COST.get());
            var recoveryInst = entity.getAttribute(ModAttributes.TIME_RECOVERY_RATE.get());

            if (maxInst != null) {
                double maxBase = switch (this.tier) {
                    case 1 -> 1000.0;
                    case 2 -> 50000.0;
                    case 3 -> 200000.0;
                    case 4 -> 1000000.0;
                    default -> 0.0;
                };
                maxInst.setBaseValue(maxBase);
            }

            if (recoveryInst != null) {
                double recoveryBase = switch (this.tier) {
                    case 1 -> 100.0;
                    case 2 -> 1000.0;
                    default -> 5000.0;
                };
                recoveryInst.setBaseValue(recoveryBase);
            }
        }

        public void copyFrom(TimeData source) {
            this.tier = source.tier;
            this.currentCost = source.currentCost;
            this.selectedSkill = source.selectedSkill;
            this.attackBonus = source.attackBonus;
            this.defenseBonus = source.defenseBonus;
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        public static final ResourceLocation IDENTIFIER = new ResourceLocation(TUTM.MODID, "time_data");
        private final TimeData data = new TimeData();
        private final LazyOptional<TimeData> optional = LazyOptional.of(() -> data);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return INSTANCE.orEmpty(cap, optional);
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag nbt = new CompoundTag();
            nbt.putInt("tier", data.tier);
            nbt.putDouble("cost", data.currentCost);
            nbt.putDouble("attackbonus", data.attackBonus);
            nbt.putDouble("defensebonus", data.defenseBonus);
            nbt.putInt("selectedSkill", data.selectedSkill);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.tier = nbt.getInt("tier");
            data.currentCost = nbt.getDouble("cost");
            data.attackBonus = nbt.getDouble("attackbonus");
            data.defenseBonus = nbt.getDouble("defensebonus");
            if (nbt.contains("selectedSkill")) {
                data.selectedSkill = nbt.getInt("selectedSkill");
            }
        }
    }
}