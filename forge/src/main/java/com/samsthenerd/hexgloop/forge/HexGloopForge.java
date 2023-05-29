package com.samsthenerd.hexgloop.forge;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("hexgloop")
public class HexGloopForge {
    public HexGloopForge(){
        // so that we can register properly with architectury
        EventBuses.registerModEventBus(HexGloop.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        HexGloop.onInitialize();
    }
}
