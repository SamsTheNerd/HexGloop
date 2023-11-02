package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IIotaProviderLocus extends ILocusModule{
    // note: you can just return a HexPattern if you want to give a pattern but need the harness
    public Iota provideIota(BlockPos pos, BlockState bs, World world, CastingHarness harness);
}
