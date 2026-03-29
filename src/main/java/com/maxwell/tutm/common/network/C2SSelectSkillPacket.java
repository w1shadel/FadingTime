package com.maxwell.tutm.common.network;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SSelectSkillPacket(int skillIndex) {
    public static void encode(C2SSelectSkillPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.skillIndex);
    }

    public static C2SSelectSkillPacket decode(FriendlyByteBuf buf) {
        return new C2SSelectSkillPacket(buf.readInt());
    }

    public static void handle(C2SSelectSkillPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                data.selectedSkill = msg.skillIndex;
            });
        });
        ctx.get().setPacketHandled(true);
    }
}