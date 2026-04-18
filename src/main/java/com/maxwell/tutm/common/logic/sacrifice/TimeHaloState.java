package com.maxwell.tutm.common.logic.sacrifice;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.UUID;

public class TimeHaloState extends SavedData {
    private UUID ownerUUID = null;

    public static TimeHaloState get(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(
                TimeHaloState::load, TimeHaloState::new, "time_halo_manager");
    }

    public static TimeHaloState load(CompoundTag tag) {
        TimeHaloState state = new TimeHaloState();
        if (tag.hasUUID("owner")) state.ownerUUID = tag.getUUID("owner");
        return state;
    }

    public UUID getOwner() {
        return ownerUUID;
    }

    public void setOwner(UUID uuid) {
        this.ownerUUID = uuid;
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        if (ownerUUID != null) tag.putUUID("owner", ownerUUID);
        return tag;
    }
}