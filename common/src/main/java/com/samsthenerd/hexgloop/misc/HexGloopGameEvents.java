package com.samsthenerd.hexgloop.misc;

import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.mixins.vibrations.MixinGameEventFrequencies;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.event.GameEvent;

public class HexGloopGameEvents {
    // use non deferred so that we can get them to register before the sculk sensor block
    public static final DeferredRegister<GameEvent> gameEvents = DeferredRegister.create(HexGloop.MOD_ID, Registry.GAME_EVENT_KEY);
    // public static final Registrar<GameEvent> gameEvents = HexGloop.REGISTRIES.get().get(Registry.GAME_EVENT_KEY);
    
    public static final Object2IntMap<Supplier<GameEvent>> ourFreqs = new Object2IntOpenHashMap<Supplier<GameEvent>>();
    
    public static final RegistrySupplier<GameEvent> CASTING_EVENT = gameEvent("casting_event", 16, 2);

    public static void register(){
        gameEvents.register();
    }

    public static void addGameEventFreq(GameEvent ge, int freq){
        Object2IntMap<GameEvent> allFreqs = new Object2IntOpenHashMap<GameEvent>(SculkSensorBlock.FREQUENCIES);
        allFreqs.put(ge, freq);
        MixinGameEventFrequencies.setFrequencies(allFreqs);
    }

    public static RegistrySupplier<GameEvent> gameEvent(String id, int range){
        return gameEvent(id, range, 1);
    }

    public static RegistrySupplier<GameEvent> gameEvent(String id, int range, int freq){
        HexGloop.logPrint("try to register " + id + " with range " + range + " and freq " + freq);
		RegistrySupplier<GameEvent> ourEvent = gameEvents.register(new Identifier(HexGloop.MOD_ID, id), 
				() -> new GameEvent(new Identifier(HexGloop.MOD_ID, id).toString(), range));
        ourFreqs.put(ourEvent, freq);
        ourEvent.listen((event) -> {
            addGameEventFreq(event, freq);
        });
        return ourEvent;
	}
}
