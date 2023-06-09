package com.samsthenerd.hexgloop.misc;

import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class HexGloopGameEvents {
    // use non deferred so that we can get them to register before the sculk sensor block
    public static final Registrar<GameEvent> gameEvents = Registries.get(HexGloop.MOD_ID).get(Registry.GAME_EVENT_KEY);
    
    public static final Object2IntOpenHashMap<Supplier<GameEvent>> ourFreqs = new Object2IntOpenHashMap<Supplier<GameEvent>>();
    
    public static final RegistrySupplier<GameEvent> CASTING_EVENT = gameEvent("casting_event", 16, 2);

    public static void register(){
    }

    public static RegistrySupplier<GameEvent> gameEvent(String id, int range){
        return gameEvent(id, range, 1);
    }

    public static RegistrySupplier<GameEvent> gameEvent(String id, int range, int freq){
        HexGloop.logPrint("try to register " + id + " with range " + range + " and freq " + freq);
		RegistrySupplier<GameEvent> ourEvent = gameEvents.register(new Identifier(HexGloop.MOD_ID, id), 
				() -> new GameEvent(new Identifier(HexGloop.MOD_ID, id).toString(), range));
        ourFreqs.put(ourEvent, freq);
        return ourEvent;
	}
}
