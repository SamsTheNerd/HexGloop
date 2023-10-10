package com.samsthenerd.hexgloop.blocks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

// for blocks that cats should sit on !
public interface ICatPost {
    default boolean ifItFits(WorldView world, BlockPos pos){
        return true;
    }
}
