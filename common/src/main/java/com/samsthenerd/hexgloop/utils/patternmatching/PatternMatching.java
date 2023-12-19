package com.samsthenerd.hexgloop.utils.patternmatching;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.util.Identifier;

public class PatternMatching {
    public static boolean match(HexPattern patA, HexPattern patB){
        return HexLine.parse(patA.anglesSignature()).equalModStrokes(HexLine.parse(patB.anglesSignature())).isPresent();
    }

    // returns ID of matching great spell or null
    public static Identifier matchGreatSpell(HexPattern patA){
        for(Identifier greatID : PatternRegistry.getAllPerWorldPatternNames()){
            try{
                HexPattern greatPat = PatternRegistry.lookupPattern(greatID).prototype();
                if(match(patA, greatPat)){
                    return greatID;
                }
            } catch (IllegalArgumentException e){
                continue;
            }
        }
        return null;
    }
}
