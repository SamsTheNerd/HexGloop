package com.samsthenerd.hexgloop.casting.wehavelociathome;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public interface IContextHelper {
    public List<BlockPos> getChestRefs();

    public void addChestRef(BlockPos pos);
}
