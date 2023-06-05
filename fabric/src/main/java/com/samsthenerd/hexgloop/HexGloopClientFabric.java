package com.samsthenerd.hexgloop;

import net.fabricmc.api.ClientModInitializer;

public class HexGloopClientFabric implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        HexGloopClient.onInitializeClient();
    }
}
