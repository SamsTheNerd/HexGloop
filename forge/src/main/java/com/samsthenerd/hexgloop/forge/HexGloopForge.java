package com.samsthenerd.hexgloop.forge;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.HexGloopClient;
import com.samsthenerd.hexgloop.forge.misc.TrinketyImplForge;
import com.samsthenerd.hexgloop.misc.TrinketyImplFake;

import dev.architectury.platform.Platform;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hexgloop")
public class HexGloopForge {
    public HexGloopForge(){
        // so that we can register properly with architectury
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(HexGloop.MOD_ID, modBus);
        modBus.addListener(this::onClientSetup);

        // setup curios
        if(Platform.isModLoaded("curios")){
            modBus.addListener(TrinketyImplForge::onInterModEnqueue);
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplForge();
        } else {
            HexGloop.TRINKETY_INSTANCE = new TrinketyImplFake();
        }

        HexGloop.onInitialize();
    }

    private void onClientSetup(FMLClientSetupEvent event) { 
        HexGloopClient.onInitializeClient();
        // event.enqueueWork(() -> {
        // });
    }
}
