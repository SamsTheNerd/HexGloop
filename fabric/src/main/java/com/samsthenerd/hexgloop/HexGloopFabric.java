package com.samsthenerd.hexgloop;

import com.samsthenerd.hexgloop.misc.BundleResourcePackFabric;
import com.samsthenerd.hexgloop.misc.TrinketyImplFabric;
import com.samsthenerd.hexgloop.misc.TrinketyImplFake;

import dev.architectury.platform.Platform;
import net.fabricmc.api.ModInitializer;

public class HexGloopFabric implements ModInitializer {
    @Override
	public void onInitialize() {
        if(Platform.isModLoaded("trinkets")){
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplFabric();
        } else {
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplFake();
        }
        HexGloop.onInitialize();
        HexGloop.GLOOPXPLAT = new GloopXPlatFabric();
        BundleResourcePackFabric.register();
    }
}