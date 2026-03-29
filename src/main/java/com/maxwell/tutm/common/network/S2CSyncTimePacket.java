package com.maxwell.tutm.common.network;

import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.logic.BossTimeManager;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.PlayerTimeMode;
import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncTimePacket {
    private final double cost, max;
    private final int tier;
    private final PlayerTimeMode pMode;
    private final BossTimeMode bMode;
    private final int bossAccel;
    private final int playerAccel;
    private final int selectedSkill;
    private final double attackBonus;
    private final double defenseBonus;

    public S2CSyncTimePacket(double cost, double max, int tier, PlayerTimeMode pMode, BossTimeMode bMode, int bossAccel, int playerAccel, int selectedSkill, double attackBonus, double defenseBonus) {
        this.cost = cost;
        this.max = max;
        this.tier = tier;
        this.pMode = pMode;
        this.bMode = bMode;
        this.bossAccel = bossAccel;
        this.playerAccel = playerAccel;
        this.selectedSkill = selectedSkill;
        this.attackBonus = attackBonus;
        this.defenseBonus = defenseBonus;
    }

    public static void encode(S2CSyncTimePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.cost);
        buf.writeDouble(msg.max);
        buf.writeInt(msg.tier);
        buf.writeEnum(msg.pMode);
        buf.writeEnum(msg.bMode);
        buf.writeInt(msg.bossAccel);
        buf.writeInt(msg.playerAccel);
        buf.writeInt(msg.selectedSkill);
        buf.writeDouble(msg.attackBonus);
        buf.writeDouble(msg.defenseBonus);
    }

    public static S2CSyncTimePacket decode(FriendlyByteBuf buf) {
        return new S2CSyncTimePacket(
                buf.readDouble(), buf.readDouble(), buf.readInt(),
                buf.readEnum(PlayerTimeMode.class), buf.readEnum(BossTimeMode.class),
                buf.readInt(), buf.readInt(), buf.readInt(),
                buf.readDouble(),
                buf.readDouble()
        );
    }

    public static void handle(S2CSyncTimePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    mc.player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                        data.selectedSkill = msg.selectedSkill;
                        data.attackBonus = msg.attackBonus;
                        data.defenseBonus = msg.defenseBonus;
                    });
                }
                TimeManager.setClientState(msg.getPMode(), msg.getPlayerAccel());
                BossTimeManager.setClientState(msg.getBMode(), msg.getBossAccel());
                TimeRenderHandler.setClientData(msg.getCost(), msg.getMax(), msg.getTier());
            });
        });
        ctx.get().setPacketHandled(true);
    }

    public double getCost() {
        return cost;
    }

    public double getMax() {
        return max;
    }

    public int getTier() {
        return tier;
    }

    public PlayerTimeMode getPMode() {
        return pMode;
    }

    public BossTimeMode getBMode() {
        return bMode;
    }

    public int getBossAccel() {
        return bossAccel;
    }

    public int getPlayerAccel() {
        return playerAccel;
    }

    public int getSelectedSkill() {
        return selectedSkill;
    }
}