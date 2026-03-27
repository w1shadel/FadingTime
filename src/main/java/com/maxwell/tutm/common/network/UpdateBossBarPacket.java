package com.maxwell.tutm.common.network;

import com.maxwell.tutm.client.gui.CustomBossBarManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateBossBarPacket {
    private final boolean shouldDisplay;
    private final float currentHealth;
    private final float maxHealth;
    private final Component bossName;
    private final boolean isSecond;

    public UpdateBossBarPacket(boolean shouldDisplay, float currentHealth, float maxHealth, Component bossName, boolean isSecond) {
        this.shouldDisplay = shouldDisplay;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.bossName = bossName;
        this.isSecond = isSecond;
    }

    public static UpdateBossBarPacket decode(FriendlyByteBuf buf) {
        boolean display = buf.readBoolean();
        if (display) {
            float current = buf.readFloat();
            float max = buf.readFloat();
            Component name = buf.readComponent();
            boolean second = buf.readBoolean();
            return new UpdateBossBarPacket(display, current, max, name, second);
        } else {
            return new UpdateBossBarPacket(false, 0, 1, Component.empty(), false);
        }
    }

    public static void handle(UpdateBossBarPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            CustomBossBarManager.handleUpdatePacket(packet);
        });
        context.setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.shouldDisplay);
        if (this.shouldDisplay) {
            buf.writeFloat(this.currentHealth);
            buf.writeFloat(this.maxHealth);
            buf.writeComponent(this.bossName);
            buf.writeBoolean(this.isSecond);
        }
    }

    public boolean shouldDisplay() {
        return shouldDisplay;
    }

    public float getCurrentHealth() {
        return currentHealth;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public Component getBossName() {
        return bossName;
    }

    public boolean isSecond() {
        return isSecond;
    }
}