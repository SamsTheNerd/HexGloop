package com.samsthenerd.hexgloop.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// indicates that the block shouldn't be returned as a raycast result
public interface ICantBeRaycasted {
    public default boolean passThrough(World world, BlockPos pos){
        return true;
    }
}
