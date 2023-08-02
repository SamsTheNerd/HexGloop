package com.samsthenerd.hexgloop.casting;

import java.util.List;
import java.util.function.Function;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.orchard.OpReadOrchard;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyReadIota;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyWriteIota;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class HexGloopRegisterPatterns {
    public static void registerPatterns(){
        HexGloopItems.CASTING_POTION_ITEM.listen(event -> registerPotionPatterns());
    }

    public static void registerPotionPatterns(){
        try{
            PatternRegistry.mapPattern(HexPattern.fromAngles("wawwedewwqqq", HexDir.EAST), 
                new Identifier(HexGloop.MOD_ID, "craft/potion"),
                new OpMakePackagedSpell<>(HexGloopItems.CASTING_POTION_ITEM.get(), MediaConstants.SHARD_UNIT));
            // qwawqwadawqwqwqwqwqw <- simpler sign write with hexagon
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwedwewdweqawqwqwqwqwqw", HexDir.SOUTH_WEST),
                new Identifier(HexGloop.MOD_ID, "set_label"),
                new OpSetLabel());
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqq", HexDir.SOUTH_EAST), 
                new Identifier(HexGloop.MOD_ID, "read_orchard"),
                new OpReadOrchard(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqqdeeeqdqeee", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "read_orchard_list"),
                new OpReadOrchard(true));
            
            // new focii related patterns:
            // pendant read/write
            PatternRegistry.mapPattern(HexPattern.fromAngles("waaqqqqqe", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "read_pendant"),
                new OpTrinketyReadIota((ctx) -> List.of("necklace"), false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("wadeeeeeq", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "write_pendant"),
                new OpTrinketyWriteIota((ctx) -> List.of("necklace"), false));
            // pendant checks
            PatternRegistry.mapPattern(HexPattern.fromAngles("waaqqqqqee", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "check_read_pendant"),
                new OpTrinketyReadIota((ctx) -> List.of("necklace"), true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("wadeeeeeqe", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "check_write_pendant"),
                new OpTrinketyWriteIota((ctx) -> List.of("necklace"), true));

            // ring basics
            Function<CastingContext, List<String>> standardRingFunc = (ctx) -> {
                if(ctx.getCastingHand() == Hand.MAIN_HAND) {
                    return List.of("offhandring", "mainhandring");
                }
                return List.of("mainhandring", "offhandring");
            };
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqqqqeawqwqwqwqwqw", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "read_ring"),
                new OpTrinketyReadIota(standardRingFunc, false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("deeeeeqdwewewewewew", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "write_ring"),
                new OpTrinketyWriteIota(standardRingFunc, false));
            
            // left hand ring
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqweeeeewqq", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "read_left_ring"),
                new OpTrinketyReadIota((ctx) -> List.of("offhandring", "mainhandring"), false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("deewqqqqqwee", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "write_left_ring"),
                new OpTrinketyWriteIota((ctx) -> List.of("offhandring", "mainhandring"), false));
            
            // right hand ring
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqqqqweeeee", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "read_right_ring"),
                new OpTrinketyReadIota((ctx) -> List.of("mainhandring", "offhandring"), false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("deeeeewqqqqq", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "write_right_ring"),
                new OpTrinketyWriteIota((ctx) -> List.of("mainhandring", "offhandring"), false));
            
            // ring checks
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwe", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "check_read_ring"),
                new OpTrinketyReadIota(standardRingFunc, true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("deeeeeqdwewewewewewq", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "check_write_ring"),
                new OpTrinketyWriteIota(standardRingFunc, true));
            
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }
}
