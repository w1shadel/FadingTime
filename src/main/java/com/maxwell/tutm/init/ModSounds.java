package com.maxwell.tutm.init;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import com.maxwell.tutm.TUTM;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TUTM.MODID);
    public static final RegistryObject<SoundEvent> TIME_STOP = SOUND_EVENTS.register("time_stop",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TUTM.MODID, "time_stop")));
    public static final RegistryObject<SoundEvent> TIME_START = SOUND_EVENTS.register("time_start",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TUTM.MODID, "time_start")));
    public static final RegistryObject<SoundEvent> TIME_END_ACCELERATION = SOUND_EVENTS.register("time_end_acceleration",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TUTM.MODID, "time_end_acceleration")));
    public static final RegistryObject<SoundEvent> TIME_ACCELERATION = SOUND_EVENTS.register("time_acceleration",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TUTM.MODID, "time_acceleration")));
    public static final RegistryObject<SoundEvent> REWIND = SOUND_EVENTS.register("rewind",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(TUTM.MODID, "rewind")));
}