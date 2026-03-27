package com.maxwell.tutm.client;

import com.maxwell.tutm.common.logic.TimeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "tutm", value = Dist.CLIENT)
public class ClientTemporalEvents {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // ENDフェーズで実行（1秒間に20回実行されるが、描画準備として重要）
        if (event.phase == TickEvent.Phase.END) {
            TimeManager.updateClientEffects();
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        // もしシェーダー内のTime（時間）を滑らかに動かしたいなら、RenderTickでUniformを更新する
        if (event.phase == TickEvent.Phase.START) {
            TimeManager.updateClientEffects();
        }
    }
}