package com.samsthenerd.hexgloop.casting.wehavelociathome;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// incase you want to not hard depend on hexgloop 
// (you'll still need to hard depend on hexcasting to extend BlockCircleComponent though)
public class LociRegistration {
    private static final Map<Block, BiFunction<BlockPos, World, ILociAtHome>> EXTERNAL_LOCI_REGISTRATION = new HashMap<>();

    public static void registerLociSupplier(Block block, BiFunction<BlockPos, World, ILociAtHome> supplier){
        if(EXTERNAL_LOCI_REGISTRATION.containsKey(block)){
            throw new IllegalArgumentException("Block " + block + " already has a loci supplier registered!");
        }
        EXTERNAL_LOCI_REGISTRATION.put(block, supplier);
    }

    @Nullable
    public static ILociAtHome getLocus(BlockState state, BlockPos pos, World world){
        Block block = state.getBlock();
        if(block instanceof ILociAtHome){
            return (ILociAtHome)block;
        }
        if(EXTERNAL_LOCI_REGISTRATION.containsKey(block)){
            return EXTERNAL_LOCI_REGISTRATION.get(block).apply(pos, world);
        }
        return null;
    }
}
