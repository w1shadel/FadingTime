package com.maxwell.tutm.common.capability;

import com.maxwell.tutm.TUTM;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = TUTM.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TimeDataCapability {
    public static final Capability<TimeData> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(TimeData.class);
    }

    public static class TimeData {
        public double currentCost = 0;
        public double maxCost = 1000000;

        public void copyFrom(TimeData source) {
            this.currentCost = source.currentCost;
            this.maxCost = source.maxCost;
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
            nbt.putDouble("cost", data.currentCost);
            nbt.putDouble("max", data.maxCost);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.currentCost = nbt.getDouble("cost");
            data.maxCost = nbt.getDouble("max");
        }
    }
}