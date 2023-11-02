package com.samsthenerd.hexgloop.casting.wehavelociathome;

import com.samsthenerd.hexgloop.mixins.lociathome.MixinExposeHarnessStuff;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPatternType;
import at.petrak.hexcasting.api.spell.casting.eval.FunctionalData;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import kotlin.Pair;

public class LociUtils {
    public static int getCircleMedia(BlockEntityAbstractImpetus impetus){
        return impetus.getMedia();
    }

    // returns whether or not it had enough to withdraw
    public static boolean withdrawCircleMedia(BlockEntityAbstractImpetus impetus, int amount){
        if(impetus.getMedia() < 0) return true; // for infinite media
        if(impetus.getMedia() >= amount){
            impetus.setMedia(impetus.getMedia() - amount);
            return true;
        }
        return false;
    }

    public static boolean addOrEmbedIota(CastingHarness harness, Iota iota){
        // so we want to either embed the iota in a new paren or add it to existing or add it to the stack?
        if(iota instanceof PatternIota patternIota){
            // if it's a pattern iota deal with it elsewhere maybe ?
            return true;
        }
        Pair<FunctionalData, ResolvedPatternType> result = ((MixinExposeHarnessStuff)(Object)harness).invokehandleParentheses(iota);
        if(result.getSecond().getSuccess()){
            harness.applyFunctionalData(result.getFirst());
            return true;
        }
        return false; // ?
    }
}
