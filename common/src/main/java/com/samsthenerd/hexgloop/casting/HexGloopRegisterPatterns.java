package com.samsthenerd.hexgloop.casting;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell;
import net.minecraft.util.Identifier;

public class HexGloopRegisterPatterns {
    public static void registerPatterns(){
        try{
            PatternRegistry.mapPattern(HexPattern.fromAngles("wawwedewwqqq", HexDir.EAST), new Identifier(HexGloop.MOD_ID, "craft/potion"),
                    new OpMakePackagedSpell<>(HexGloopItems.CASTING_POTION_ITEM.get(), MediaConstants.CRYSTAL_UNIT));
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }
}
