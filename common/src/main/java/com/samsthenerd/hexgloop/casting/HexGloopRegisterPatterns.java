package com.samsthenerd.hexgloop.casting;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.orchard.OpReadOrchard;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell;
import net.minecraft.util.Identifier;

public class HexGloopRegisterPatterns {
    public static void registerPatterns(){
        HexGloopItems.CASTING_POTION_ITEM.listen(event -> registerPotionPatterns());
    }

    public static void registerPotionPatterns(){
        try{
            PatternRegistry.mapPattern(HexPattern.fromAngles("wawwedewwqqq", HexDir.EAST), new Identifier(HexGloop.MOD_ID, "craft/potion"),
                    new OpMakePackagedSpell<>(HexGloopItems.CASTING_POTION_ITEM.get(), MediaConstants.CRYSTAL_UNIT));
            // qwawqwadawqwqwqwqwqw <- simpler sign write with hexagon
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwedwewdweqawqwqwqwqwqw", HexDir.SOUTH_WEST), new Identifier(HexGloop.MOD_ID, "set_label"),
                    new OpSetLabel());
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqq", HexDir.SOUTH_EAST), new Identifier(HexGloop.MOD_ID, "read_orchard"),
                    new OpReadOrchard(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqqdeeeqdqeee", HexDir.SOUTH_EAST), new Identifier(HexGloop.MOD_ID, "read_orchard_list"),
                    new OpReadOrchard(true));
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }
}
