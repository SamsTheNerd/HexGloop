package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMediaProviderLocus extends ILocusModule{
    public int getProvideableMedia(BlockPos pos, BlockState bs, World world, BlockEntityAbstractImpetus impetus);

    // returns how much it actually withdrew
    public int withdrawProvideableMedia(BlockPos pos, BlockState bs, World world, BlockEntityAbstractImpetus impetus, int amount);
}
