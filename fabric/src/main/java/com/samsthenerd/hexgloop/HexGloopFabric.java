package com.samsthenerd.hexgloop;

import net.fabricmc.api.ModInitializer;

public class HexGloopFabric implements ModInitializer {
    @Override
	public void onInitialize() {
        HexGloop.onInitialize();
    }
}