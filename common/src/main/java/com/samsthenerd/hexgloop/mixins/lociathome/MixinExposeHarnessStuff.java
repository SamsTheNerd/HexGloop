package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.casting.eval.FunctionalData;
import at.petrak.hexcasting.api.spell.iota.Iota;
import kotlin.Pair;

@Mixin(CastingHarness.class)
public interface MixinExposeHarnessStuff {
    @Invoker(value="handleParentheses", remap = false)
    public Pair<FunctionalData, ResolvedPatternType> invokehandleParentheses(Iota iota);
}
