package com.samsthenerd.hexgloop;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Suppliers;
import com.samsthenerd.hexgloop.casting.HexGloopRegisterPatterns;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.misc.HexGloopGameEvents;
import com.samsthenerd.hexgloop.misc.ITrinkety;
import com.samsthenerd.hexgloop.network.HexGloopNetwork;
import com.samsthenerd.hexgloop.recipes.HexGloopRecipes;

import dev.architectury.registry.registries.Registries;

public class HexGloop {
    public static final String MOD_ID = "hexgloop";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ITrinkety TRINKETY_INSTANCE;

    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));

	public static final void logPrint(String message){
		LOGGER.info(message);
	}


    public static void onInitialize() {
        HexGloopItems.registerItems();
        HexGloopRecipes.init();
        HexGloopNetwork.register();
        HexGloopRegisterPatterns.registerPatterns();
        HexGloopGameEvents.register();
    }
}
