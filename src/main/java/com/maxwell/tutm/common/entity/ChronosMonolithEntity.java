package com.maxwell.tutm.common.entity;

import com.maxwell.tutm.common.util.AutoRegisterEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

@AutoRegisterEntity(
        name = "chronos_monolith",
        width = 1.0f, height = 5.0f,
        renderer = "com.maxwell.tutm.client.renderer.ChronosMonolithRenderer"
)
public class ChronosMonolithEntity extends LivingEntity implements ITUTMEntity {
    private Vec3 fixedPos;
    private int waveTimer = 0;

    public ChronosMonolithEntity(EntityType<? extends LivingEntity> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    public void tick() {
        if (fixedPos == null) {
            fixedPos = this.position();
        }
        this.setPos(fixedPos.x, fixedPos.y, fixedPos.z);
        this.setDeltaMovement(Vec3.ZERO);
        super.tick();
        if (this.level().isClientSide) return;
        waveTimer++;
        if (waveTimer >= 100) {
            DivineWaveEntity wave = new DivineWaveEntity(this.level(), this);
            this.level().addFreshEntity(wave);
            waveTimer = 0;
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if (fixedPos != null) {
            nbt.putDouble("fX", fixedPos.x);
            nbt.putDouble("fY", fixedPos.y);
            nbt.putDouble("fZ", fixedPos.z);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("fX")) {
            fixedPos = new Vec3(nbt.getDouble("fX"), nbt.getDouble("fY"), nbt.getDouble("fZ"));
        }
    }

    @Override
    public Iterable<net.minecraft.world.item.ItemStack> getArmorSlots() {
        return java.util.Collections.emptyList();
    }

    @Override
    public net.minecraft.world.entity.HumanoidArm getMainArm() {
        return net.minecraft.world.entity.HumanoidArm.RIGHT;
    }

    @Override
    public net.minecraft.world.item.ItemStack getItemBySlot(net.minecraft.world.entity.EquipmentSlot s) {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(net.minecraft.world.entity.EquipmentSlot s, net.minecraft.world.item.ItemStack st) {
    }
}