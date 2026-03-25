package com.maxwell.tutm.common.network;

import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CSyncTimePacket {
    private final double cost, max;
    private final boolean stopped, rewinding;
    private final int acceleration;

    public S2CSyncTimePacket(double cost, double max, boolean stopped, int acceleration, boolean rewinding) {
        this.cost = cost;
        this.max = max;
        this.stopped = stopped;
        this.acceleration = acceleration;
        this.rewinding = rewinding;
    }

    public static void encode(S2CSyncTimePacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.cost);
        buf.writeDouble(msg.max);
        buf.writeBoolean(msg.stopped);
        buf.writeInt(msg.acceleration);
        buf.writeBoolean(msg.rewinding);
    }

    public static S2CSyncTimePacket decode(FriendlyByteBuf buf) {
        return new S2CSyncTimePacket(buf.readDouble(), buf.readDouble(), buf.readBoolean(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(S2CSyncTimePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TimeManager.setClientData(msg.cost, msg.max, msg.stopped, msg.acceleration, msg.rewinding);
            TimeManager.updateClientEffects();
        });
        ctx.get().setPacketHandled(true);
    }
}