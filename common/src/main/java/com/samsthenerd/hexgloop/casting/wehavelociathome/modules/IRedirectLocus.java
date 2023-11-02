package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import java.util.List;
import java.util.Set;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRedirectLocus extends ILocusModule {
    public BlockPos forceNextPos(BlockPos currentPos, BlockState currentState, World world, 
        List<BlockPos> trackedBlocks, Set<BlockPos> knownBlocks);
}
