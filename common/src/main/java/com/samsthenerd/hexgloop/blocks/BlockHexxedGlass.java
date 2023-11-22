package com.samsthenerd.hexgloop.blocks;

import net.minecraft.block.GlassBlock;
import net.minecraft.block.Stainable;
import net.minecraft.util.DyeColor;

public class BlockHexxedGlass extends GlassBlock implements ICantBeRaycasted, Stainable{
    
    public BlockHexxedGlass(Settings settings){
        super(settings);
    }

    public DyeColor getColor(){
        return DyeColor.PINK;
    }
}
