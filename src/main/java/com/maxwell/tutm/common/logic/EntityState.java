package com.maxwell.tutm.common.logic;
import net.minecraft.world.phys.Vec3;

public record EntityState(Vec3 pos, Vec3 delta, float yRot, float xRot, float health) {}