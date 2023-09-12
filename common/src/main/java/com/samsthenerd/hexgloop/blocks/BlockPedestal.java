package com.samsthenerd.hexgloop.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPedestal extends BlockWithEntity{
    public BlockPedestal(Settings settings) {
        super(settings);
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new BlockEntityPedestal(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != HexGloopBEs.PEDESTAL_BE.get()) return null;
        return (_world, _pos, _state, _be) -> ((BlockEntityPedestal)_be).tick(_world, _pos, _state);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType( @Nonnull BlockState state )
    {
        return BlockRenderType.MODEL;
    }
}
