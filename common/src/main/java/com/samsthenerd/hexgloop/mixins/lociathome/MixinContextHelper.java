package com.samsthenerd.hexgloop.mixins.lociathome;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.wehavelociathome.IContextHelper;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.util.math.BlockPos;

@Mixin(CastingContext.class)
public class MixinContextHelper implements IContextHelper{
    private List<BlockPos> chestRefs = new ArrayList<BlockPos>();

    public List<BlockPos> getChestRefs(){
        return new ArrayList<BlockPos>(chestRefs);
    }

    public void addChestRef(BlockPos pos){
        chestRefs.add(pos);
    }
}
