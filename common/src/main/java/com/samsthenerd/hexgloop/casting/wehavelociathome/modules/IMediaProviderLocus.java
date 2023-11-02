package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IMediaProviderLocus extends ILocusModule{
    public ADMediaHolder getMediaHolder(BlockPos pos, World world, BlockEntityAbstractImpetus impetus);
}
