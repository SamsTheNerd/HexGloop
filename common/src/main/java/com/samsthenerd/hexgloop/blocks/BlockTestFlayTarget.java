package com.samsthenerd.hexgloop.blocks;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;

public class BlockTestFlayTarget extends Block implements IDynamicFlayTarget {
    public BlockTestFlayTarget(Settings settings) {
        super(settings);
    }

    @Override
    public void absorbVillagerMind(VillagerEntity sacrifice, BlockPos flayPos, CastingContext ctx){
        HexGloop.logPrint("absorbed villager mind !");
    }
    
    @Override
    public boolean canAcceptMind(VillagerEntity sacrifice, BlockPos flayPos, CastingContext ctx){
        HexGloop.logPrint("can accept villager mind !");
        return true;
    }
}
