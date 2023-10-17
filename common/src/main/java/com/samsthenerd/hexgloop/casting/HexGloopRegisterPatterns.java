package com.samsthenerd.hexgloop.casting;

import java.util.List;
import java.util.function.Function;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.casting.dimensions.OpIsInDimension;
import com.samsthenerd.hexgloop.casting.gloopifact.OpReadGloopifact;
import com.samsthenerd.hexgloop.casting.gloopifact.OpSyncRavenmindGloopifact;
import com.samsthenerd.hexgloop.casting.gloopifact.OpWriteGloopifact;
import com.samsthenerd.hexgloop.casting.mirror.OpBindMirror;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpEvalCatchMishap;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpHahaFunnyAssertQEDGetItLikeTheMathProofLol;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpRevealLastMishap;
import com.samsthenerd.hexgloop.casting.orchard.OpReadOrchard;
import com.samsthenerd.hexgloop.casting.redstone.OpConjureRedstone;
import com.samsthenerd.hexgloop.casting.redstone.OpGetComparator;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyReadIota;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyWriteIota;
import com.samsthenerd.hexgloop.casting.truenameclassaction.OpGetCoinBinder;
import com.samsthenerd.hexgloop.casting.truenameclassaction.OpRefreshTruename;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.casting.operators.spells.OpMakePackagedSpell;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class HexGloopRegisterPatterns {
    public static void registerPatterns(){
        HexGloopItems.CASTING_POTION_ITEM.listen(event -> registerItemDependentPatterns());
        HexGloopItems.FOCAL_RING.listen(event -> registerTrinketyFociiPatterns());
        HexGloopBlocks.CONJURED_REDSTONE_BLOCK.listen(event -> registerRedstonePatterns());
        // non item dependent stuff: 
        try{
            // orchard patterns
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqq", HexDir.SOUTH_EAST), 
                new Identifier(HexGloop.MOD_ID, "read_orchard"),
                new OpReadOrchard(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("dqqqqqdeeeqdqeee", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "read_orchard_list"),
                new OpReadOrchard(true));
            
            // dimension checks
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqawqadaqdeeweweweew", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "in_overworld"),
                new OpIsInDimension(World.OVERWORLD));
            PatternRegistry.mapPattern(HexPattern.fromAngles("eaqawqadaqdeewewewe", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "in_nether"),
                new OpIsInDimension(World.NETHER));

            PatternRegistry.mapPattern(HexPattern.fromAngles("ddeaqq", HexDir.NORTH_EAST), 
                new Identifier(HexGloop.MOD_ID, "opnop_useless"), 
                new OpNop());

            PatternRegistry.mapPattern(HexPattern.fromAngles("wawaw", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "check_ambit"),
                new OpCheckAmbit());

            // catchy eval
            PatternRegistry.mapPattern(HexPattern.fromAngles("dweaqqw", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "catchy_eval"),
                OpEvalCatchMishap.INSTANCE);
            PatternRegistry.mapPattern(HexPattern.fromAngles("dweaqqqqa", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "reveal_mishap"),
                OpRevealLastMishap.INSTANCE);

            PatternRegistry.mapPattern(HexPattern.fromAngles("qed", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "assert"),
                new OpHahaFunnyAssertQEDGetItLikeTheMathProofLol());

            PatternRegistry.mapPattern(HexPattern.fromAngles("qewdweeddw", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopimind_upload"),
                new OpSyncRavenmindGloopifact(true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("qedadewdde", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopimind_download"),
                new OpSyncRavenmindGloopifact(false));
            
            // bound mirror pedestal stuff:
            // normal bind: deeeedwwdwdw, north east
            PatternRegistry.mapPattern(HexPattern.fromAngles("deeeedwwdwdw", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "bind_mirror"),
                new OpBindMirror(false));
            // temp bind  : aqqqqawwawaw, north west
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqqqawwawaw", HexDir.NORTH_WEST),
                new Identifier(HexGloop.MOD_ID, "temp_bind_mirror"),
                new OpBindMirror(true));

            // hotbar patterns
            PatternRegistry.mapPattern(HexPattern.fromAngles("dwewdwe", HexDir.WEST), 
                new Identifier(HexGloop.MOD_ID, "set_hotbar_slot"),
                new OpHotbar(false, false));

            PatternRegistry.mapPattern(HexPattern.fromAngles("qwawqwa", HexDir.EAST), 
                new Identifier(HexGloop.MOD_ID, "get_hotbar_slot"),
                new OpHotbar(false, true));

            PatternRegistry.mapPattern(HexPattern.fromAngles("qwawqwadawqwa", HexDir.EAST), 
                new Identifier(HexGloop.MOD_ID, "swap_hotbar_slot"),
                new OpHotbar(true, false));
            
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }

    private static void registerItemDependentPatterns(){
        try{
            PatternRegistry.mapPattern(HexPattern.fromAngles("wawwedewwqqq", HexDir.EAST), 
                new Identifier(HexGloop.MOD_ID, "craft/potion"),
                new OpMakePackagedSpell<>(HexGloopItems.CASTING_POTION_ITEM.get(), MediaConstants.SHARD_UNIT));

            // wwaadaqwaweqwqwawewewawqwwwwwadeeeeeqww
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwaadaqwaweqqwaweewawqwwwwadeeeeeqww", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "craft/gloopifact"),
                new OpMakePackagedSpell<>(HexGloopItems.GLOOPIFACT_ITEM.get(), 0));
                

            // craft sword: waqqqqqwwwaqwwwwaq
            PatternRegistry.mapPattern(HexPattern.fromAngles("waqqqqqwwwaqwwwwaq", HexDir.EAST),
                new Identifier(HexGloop.MOD_ID, "craft/hex_blade"),
                new OpMakePackagedSpell<>(HexGloopItems.HEX_BLADE_ITEM.get(), MediaConstants.SHARD_UNIT));

            // craft shoe things: waqqqqqwwaqwdwqaw
                
            // gloopifact patterns
            PatternRegistry.mapPattern(HexPattern.fromAngles("aqqqqqeqadaqw", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopifact_read"),
                new OpReadGloopifact(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("deeeeeqqadaqw", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopifact_write"),
                new OpWriteGloopifact(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwqadaqwwaqqqqq", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopifact_check_read"),
                new OpReadGloopifact(true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwqadaqwwdeeeee", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "gloopifact_check_write"),
                new OpWriteGloopifact(true));
            

            // qwawqwadawqwqwqwqwqw <- simpler sign write with hexagon
            PatternRegistry.mapPattern(HexPattern.fromAngles("wwedwewdweqawqwqwqwqwqw", HexDir.SOUTH_WEST),
                new Identifier(HexGloop.MOD_ID, "set_label"),
                new OpSetLabel());

            // snack
            PatternRegistry.mapPattern(HexPattern.fromAngles("eeewdw", HexDir.SOUTH_WEST),
                new Identifier(HexGloop.MOD_ID, "conjure_tasty_treat"),
                new OpConjureTastyTreat());
            
            // for coins really, i mean i guess could be expanded to other stuff later though ?
            PatternRegistry.mapPattern(HexPattern.fromAngles("qaqqaqqqqq", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "get_item_bound_caster"),
                new OpGetCoinBinder(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("qaqqaqqqqqead", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "compare_item_bound_caster"),
                new OpGetCoinBinder(true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("qaqqwawqwqwqwqwqw", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "cooler_get_item_bound_caster"),
                new OpGetCoinBinder(false, true));
            PatternRegistry.mapPattern(HexPattern.fromAngles("qaqqwawqwqwqwqwqwead", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "cooler_compare_item_bound_caster"),
                new OpGetCoinBinder(true, true));
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }

    private static void registerRedstonePatterns(){
        try {
            // redstone stuff
            PatternRegistry.mapPattern(HexPattern.fromAngles("ddwwdwwdd", HexDir.SOUTH_EAST),
                new Identifier(HexGloop.MOD_ID, "read_comparator"),
                new OpGetComparator(false));
            PatternRegistry.mapPattern(HexPattern.fromAngles("aawwawwaa", HexDir.SOUTH_WEST),
                new Identifier(HexGloop.MOD_ID, "read_redstone"),
                new OpGetComparator(true));

            PatternRegistry.mapPattern(HexPattern.fromAngles("qqadd", HexDir.NORTH_EAST),
                new Identifier(HexGloop.MOD_ID, "conjured_redstone"),
                new OpConjureRedstone());
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }

    private static void registerTrinketyFociiPatterns(){
        try {
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

            PatternRegistry.mapPattern(HexPattern.fromAngles("wwwdwwwdwqqaqwedeewawwwawww", HexDir.SOUTH_WEST),
                new Identifier(HexGloop.MOD_ID, "clear_truename"),
                new OpRefreshTruename());
        } catch (PatternRegistry.RegisterPatternException exn) {
            exn.printStackTrace();
        }
    }
}
