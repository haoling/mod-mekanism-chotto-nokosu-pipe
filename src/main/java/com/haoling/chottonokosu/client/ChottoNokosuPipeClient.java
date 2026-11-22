package com.haoling.chottonokosu.client;

import com.haoling.chottonokosu.ChottoNokosuPipeMod;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ChottoNokosuPipeMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChottoNokosuPipeClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ChottoNokosuPipeMod.PIPE_MENU.get(), ChottoNokosuPipeScreen::new);
    }
}
