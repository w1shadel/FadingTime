package com.maxwell.tutm.common.network;

import com.maxwell.tutm.client.renderer.TimeRenderHandler;
import com.maxwell.tutm.common.logic.BossTimeMode;
import com.maxwell.tutm.common.logic.ClientTimeData;
import com.maxwell.tutm.common.logic.PlayerTimeMode;
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

    // 全情報を網羅するコンストラクタ
    public S2CSyncTimePacket(double cost, double max, int tier, PlayerTimeMode pMode, BossTimeMode bMode, int bossAccel) {
        this.cost = cost;
        this.max = max;
        this.tier = tier;
        this.pMode = pMode;
        this.bMode = bMode;
        this.bossAccel = bossAccel;
    }

    public static void encode(S2CSyncTimePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.cost);
        buf.writeDouble(msg.max);
        buf.writeInt(msg.tier);
        buf.writeEnum(msg.pMode);
        buf.writeEnum(msg.bMode);
        buf.writeInt(msg.bossAccel);
    }

    public static S2CSyncTimePacket decode(FriendlyByteBuf buf) {
        return new S2CSyncTimePacket(
                buf.readDouble(),
                buf.readDouble(),
                buf.readInt(),
                buf.readEnum(PlayerTimeMode.class),
                buf.readEnum(BossTimeMode.class),
                buf.readInt()
        );
    }
    public static void handle(S2CSyncTimePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientTimeData.update(msg.getPMode(), msg.getBMode(), msg.getBossAccel());
                TimeRenderHandler.setClientData(msg.getCost(), msg.getMax(), msg.getTier(), msg.getBossAccel());
            });
        });
        ctx.get().setPacketHandled(true);
    }
    public double getCost() { return cost; }
    public double getMax() { return max; }
    public int getTier() { return tier; }
    public PlayerTimeMode getPMode() { return pMode; }
    public BossTimeMode getBMode() { return bMode; }
    public int getBossAccel() { return bossAccel; }
}