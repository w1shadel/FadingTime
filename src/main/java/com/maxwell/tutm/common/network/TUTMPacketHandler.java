package com.maxwell.tutm.common.network;

import com.maxwell.tutm.TUTM;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TUTMPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TUTM.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    private static int packetId = 0;

    public static void register() {
        INSTANCE.messageBuilder(S2CSyncTimePacket.class, packetId++)
                .encoder(S2CSyncTimePacket::encode)
                .decoder(S2CSyncTimePacket::decode)
                .consumerMainThread(S2CSyncTimePacket::handle)
                .add();
    }
}