package com.samsthenerd.hexgloop.blocks.iotic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * API class for getting ADIotaHolder from blocks in a world
 *
 * `findIotaHolder` is all you need to get an iota holder
 * 
 * There are a few ways to add your own provider though:
 * 
 * - If your mod hard depends on gloop you can implement IIoticProvider on your block or block entity
 * 
 * - If your mod optionally depends on gloop (or you're adding a provider for another mod's block) you can register a provider for your block with `registerIoticProvider`
 * 
 * - If your mod doesn't depend on gloop (or if you're not wrapping an existing iota holder) you can just implement ADIotaHolder on your block or block entity. 
 */
public class IoticHandler {
    @Nullable
    public static ADIotaHolder findIotaHolder(World world, BlockPos pos){
        // first try to get a provider from the block
        BlockState state = world.getBlockState(pos);
        if(state.getBlock() instanceof IIoticProvider provider){
            return provider.getIotaHolder(world, pos);
        }
        // then try to get a provider from the block entity
        if(world.getBlockEntity(pos) instanceof IIoticProvider beProvider){
            return beProvider.getIotaHolder(world, pos);
        }
        // try to get it from the registry
        if(ioticProviders.containsKey(state.getBlock())){
            return ioticProviders.get(state.getBlock()).getIotaHolder(world, pos);
        }
        // last case check for direct ADIotaHolder implementation
        if(state.getBlock() instanceof ADIotaHolder holder){
            return holder;
        }
        // same but for the be
        if(world.getBlockEntity(pos) instanceof ADIotaHolder holder){
            return holder;
        }
        return null; // can't find anything
    }

    public static Map<Block, IIoticProvider> ioticProviders = new HashMap<>();


    /*
     * Only needed if IIoticProvider is not implemented on the block or block entity
     * Returns: true if the provider was registered, false if the block already had a provider
     */
    public static boolean registerIoticProvider(Block block, IIoticProvider provider){
        if(ioticProviders.containsKey(block)){
            return false;
        }
        ioticProviders.put(block, provider);
        return true;
    }
}
