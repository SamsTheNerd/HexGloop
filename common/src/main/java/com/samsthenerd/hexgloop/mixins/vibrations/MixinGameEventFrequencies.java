package com.samsthenerd.hexgloop.mixins.vibrations;

import java.util.function.Supplier;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.misc.HexGloopGameEvents;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.SculkSensorBlock;
import net.minecraft.world.event.GameEvent;

// register all our frequencies
@Mixin(SculkSensorBlock.class)
public class MixinGameEventFrequencies {
    @ModifyArg(method = "<clinit>", 
        at= @At(value = "INVOKE", opcode = Opcodes.INVOKESTATIC, target = "it/unimi/dsi/fastutil/objects/Object2IntMaps.unmodifiable (Lit/unimi/dsi/fastutil/objects/Object2IntMap;)Lit/unimi/dsi/fastutil/objects/Object2IntMap;"))
    private static Object2IntMap<GameEvent> modifyVibrationFreqsMap(Object2IntMap<GameEvent> map) {
        HexGloop.logPrint("try to add freqs");
        for(Supplier<GameEvent> ge : HexGloopGameEvents.ourFreqs.keySet()){
            map.put(ge.get(), HexGloopGameEvents.ourFreqs.getInt(ge));
        }
        return map;
    }
}
