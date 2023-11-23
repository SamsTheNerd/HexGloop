package com.samsthenerd.hexgloop.casting;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiFunction;

import com.samsthenerd.hexgloop.blocks.BlockSentinelBed;
import com.samsthenerd.hexgloop.items.ItemCopingSaw;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.data.client.BlockStateVariantMap.TriFunction;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

// registry for various context modifiers, handled by mixins
public class ContextModificationHandlers {
    public static Comparator<Pair<?, Integer>> comparator = (Pair<?, Integer> a, Pair<?, Integer> b) -> {
        return b.getRight().compareTo(a.getRight());
    };

    public static Queue<Pair<BiFunction<CastingContext, Boolean, Modification>, Integer>> ENLIGHTENMENT_MODIFIERS = new PriorityQueue<>(comparator);
    public static Queue<Pair<BiFunction<CastingContext, Boolean, Modification>, Integer>> OVERCAST_MODIFIERS = new PriorityQueue<>(comparator);
    public static Queue<Pair<TriFunction<CastingContext, Vec3d, Boolean, Modification>, Integer>> AMBIT_MODIFIERS = new PriorityQueue<>(comparator);

    public static enum Modification {
        NONE,
        ENABLE,
        DISABLE
    }

    public static void registerEnlightenmentModifier(BiFunction<CastingContext, Boolean, Modification> modifier, int ordering){
        ENLIGHTENMENT_MODIFIERS.add(new Pair<>(modifier, ordering));
    }

    public static void registerOvercastModifier(BiFunction<CastingContext, Boolean, Modification> modifier, int ordering){
        OVERCAST_MODIFIERS.add(new Pair<>(modifier, ordering));
    }

    public static void registerAmbitModifier(TriFunction<CastingContext, Vec3d, Boolean, Modification> modifier, int ordering){
        AMBIT_MODIFIERS.add(new Pair<>(modifier, ordering));
    }

    public static void init(){
        registerAmbitModifier(BlockSentinelBed::ambitModifier, 0);
        registerOvercastModifier(ItemCopingSaw::overcastModifer, 100000);
    }
}
