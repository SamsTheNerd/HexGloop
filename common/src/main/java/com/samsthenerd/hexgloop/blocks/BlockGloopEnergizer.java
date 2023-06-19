package com.samsthenerd.hexgloop.blocks;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityGloopEnergizer;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockGloopEnergizer extends BlockWithEntity{
    public static final int BOILER_RADIUS = 4;

    public BlockGloopEnergizer(Settings settings) {
        super(settings);
    }

    public static Set<BlockPos> getAdjacentWater(World world, BlockPos startPos){
        Set <BlockPos> waterBlocks = new HashSet<BlockPos>();
        Stack<BlockPos> queue = new Stack<BlockPos>();
        queue.push(startPos);
        while(!queue.empty()){
            BlockPos pos = queue.pop();
            for(Direction dir : Direction.values()){
                BlockPos newPos = pos.offset(dir);
                if(world.isWater(newPos) && !waterBlocks.contains(newPos) && newPos.getManhattanDistance(startPos) <= BOILER_RADIUS){
                    waterBlocks.add(newPos);
                    queue.push(newPos);
                }
            }
        }
        return waterBlocks;
    }

    // null if none exists
    public static BlockPos getNearestEnergizer(World world, BlockPos startPos){
        Set <BlockPos> waterBlocks = new HashSet<BlockPos>();
        Stack<BlockPos> queue = new Stack<BlockPos>();
        if(world.getBlockState(startPos).getBlock() instanceof BlockGloopEnergizer){
            return startPos;
        }
        queue.push(startPos);
        while(!queue.empty()){
            BlockPos pos = queue.pop();
            for(Direction dir : Direction.values()){
                BlockPos newPos = pos.offset(dir);
                if(world.getBlockState(newPos).getBlock() instanceof BlockGloopEnergizer){
                    return newPos;
                }
                if(world.isWater(newPos) && !waterBlocks.contains(newPos) && newPos.getManhattanDistance(startPos) <= BOILER_RADIUS){
                    waterBlocks.add(newPos);
                    queue.push(newPos);
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new BlockEntityGloopEnergizer(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type != HexGloopBEs.GLOOP_ENERGIZER_BE.get()) return null;
        return (_world, _pos, _state, _be) -> ((BlockEntityGloopEnergizer)_be).tick(_world, _pos, _state);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType( @Nonnull BlockState state )
    {
        return BlockRenderType.MODEL;
    }
}
