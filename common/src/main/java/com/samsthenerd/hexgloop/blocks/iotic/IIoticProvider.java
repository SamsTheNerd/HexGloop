package com.samsthenerd.hexgloop.blocks.iotic;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IIoticProvider {
    public ADIotaHolder getIotaHolder(World world, BlockPos pos);
}
