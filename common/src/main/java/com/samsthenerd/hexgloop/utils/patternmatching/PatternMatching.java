package com.samsthenerd.hexgloop.utils.patternmatching;



import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.casting.SpecialPatterns;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.PatternNameHelper;
import net.minecraft.text.Text;
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

    @Nullable
    public static Identifier getIdentifier(HexPattern pat){
        try{
            Pair<Action, Identifier> found = PatternRegistry.matchPatternAndID(pat, null);
            return found.getSecond();
        } catch (Throwable e) {
        }
        // could be a special pattern
        if (pat.sigsEqual(SpecialPatterns.CONSIDERATION)) {
			return HexAPI.modLoc("escape");
		} else if (pat.sigsEqual(SpecialPatterns.INTROSPECTION)) {
			return HexAPI.modLoc("open_paren");
		} else if (pat.sigsEqual(SpecialPatterns.RETROSPECTION)) {
			return HexAPI.modLoc("close_paren");
		}
        // not sure that this is actually gonna do anything that the above doesn't
        Action foundAction = PatternRegistry.lookupPatternByShape(pat);
        if(foundAction != null){
            return PatternRegistry.lookupPattern(foundAction);
        }
        // or a handler ?

        // otherwise must be a great spell - or nothing
        return matchGreatSpell(pat);
    }

    public static Text getName(HexPattern pat){
        // should get name for numbers/bookkeepers, normal patterns, and 
        Action foundAction = PatternRegistry.lookupPatternByShape(pat);
        if(foundAction != null){
            return foundAction.getDisplayName();
        }
        // handles great spells
        Identifier maybeGreatSpell = PatternMatching.matchGreatSpell(pat);
        if(maybeGreatSpell != null){
            return PatternRegistry.lookupPattern(maybeGreatSpell).action().getDisplayName();
        }
        // finally handles special intro/retro/consideration
        return PatternNameHelper.representationForPattern(pat);
    }
}
