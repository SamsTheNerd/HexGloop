package com.samsthenerd.hexgloop.casting.wehavelociathome;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// meant to be implemented on a Block class.
// probably going to be heavily reworked for 1.20 port so just,, don't implement too much with it
// also not really a good way to stop the circle 
public interface ILociAtHome {
    // override this to add whatever logic or probably better to just have it return some variable 
    // you modify later if there's an error
    default public boolean shouldStopCircle(){
        return false;
    }

    default public Text getStopCircleError(){
        return null;
    }

    // try not to break stuff + try to be aware of consideration-type stuff i guess
    public void rawLociCall(BlockPos pos, BlockState bs, World world, CastingHarness harness);

    // can be called in favor of BlockCircleComponent's getPattern if you want access to the harness/context for some reason
    default public HexPattern betterGetPattern(BlockPos pos, BlockState bs, World world, CastingHarness harness){
        return null;
    }

    // make this true if you override betterGetPattern
    default public boolean hasBetterGetPattern(){
        return false;
    }
}
