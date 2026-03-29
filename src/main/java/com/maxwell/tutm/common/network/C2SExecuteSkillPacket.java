package com.maxwell.tutm.common.network;

import com.maxwell.tutm.common.capability.TimeDataCapability;
import com.maxwell.tutm.common.logic.TimeManager;
import com.maxwell.tutm.common.util.CurioUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record C2SExecuteSkillPacket(int actionType) {
    public static void encode(C2SExecuteSkillPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.actionType);
    }

    public static C2SExecuteSkillPacket decode(FriendlyByteBuf buf) {
        return new C2SExecuteSkillPacket(buf.readInt());
    }

    public static void handle(C2SExecuteSkillPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !CurioUtil.hasAnyTimeItem(player)) return;
            player.getCapability(TimeDataCapability.INSTANCE).ifPresent(data -> {
                if (msg.actionType == 1) {
                    TimeManager.executeSelectedSkill(player, data.selectedSkill);
                }
            });
        });
        ctx.get().setPacketHandled(true);
    }

}