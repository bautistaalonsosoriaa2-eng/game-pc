package com.gamerpc.client;

import com.gamerpc.PCHooks;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class GamerPCClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PCHooks.opener = internet -> MinecraftClient.getInstance().setScreen(new PCScreen(internet));
    }
}
