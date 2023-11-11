package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import java.util.List;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISpeedLocus extends ILocusModule{
    public double modifyTickDelay(int blocksAgo, double currentModifier, int originalSpeed, 
        int similarBlockCount, List<BlockPos> trackedBlocks, World world, BlockEntityAbstractImpetus impetus);

    // just some utils since the modifier stuff is hard to work with

    // NOTE: target speed is a multiplier, unlike the modifiers.
    // so 1 is normal speed, 2 is twice as fast, and 1.5 is 50% faster
    public static double modifierForTarget(double targetSpeed, double currentModifier){
        return 1 / (targetSpeed * currentModifier);
    }
}
