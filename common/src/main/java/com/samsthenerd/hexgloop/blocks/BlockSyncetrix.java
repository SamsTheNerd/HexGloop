package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.IRedirectLocus;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class BlockSyncetrix extends BlockCircleComponent implements ILociAtHome, IRedirectLocus{

    public static DirectionProperty FACING = Properties.FACING;
    public static final int SYNC_RANGE = 16;
    
    public BlockSyncetrix(Settings settings) {
        super(settings);
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(FACING, Direction.NORTH)
        );
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(FACING, ctx.getPlayerLookDirection());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> pBuilder) {
        super.appendProperties(pBuilder);
        pBuilder.add(FACING);
    }


    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos,
        BlockState bs, World world){
        return true;
    }

    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world){
        return EnumSet.noneOf(Direction.class);
    }

    public BlockPos forceNextPos(BlockPos currentPos, BlockState currentState, World world, 
        List<BlockPos> trackedBlocks, Set<BlockPos> knownBlocks){
        BlockPos immediatePos = currentPos.offset(currentState.get(FACING));
        BlockState immediateState = world.getBlockState(immediatePos);
        // try to find an immediate block if there is one
        if(immediateState.getBlock() instanceof BlockCircleComponent component){
            HexGloop.logPrint("found immediate block: " + immediateState.getBlock());
            for(Direction dir : Direction.values()){
                HexGloop.logPrint("checking direction: " + dir);
                if(dir.equals(currentState.get(FACING))){
                    HexGloop.logPrint("skipping opposite direction");
                    continue; // don't get it if you have to flip
                }
                if(component.canEnterFromDirection(currentState.get(FACING).getOpposite(), dir, currentPos, immediateState, world)){
                    HexGloop.logPrint("found valid path");
                    return immediatePos;
                }
                HexGloop.logPrint("no valid path");
            }
        }
        // find another syncetrix
        for(int i = 1; i <= SYNC_RANGE; i++){
            BlockPos checkPos = currentPos.offset(currentState.get(FACING), i);
            BlockState checkState = world.getBlockState(checkPos);
            if(checkState.getBlock() instanceof BlockSyncetrix){
                return checkPos;
            }
        }
        return null; 
    }

    // it's not really a directional block
    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft){
        return bs.get(FACING);
    }

    public HexPattern getPattern(BlockPos pos, BlockState bs, World world){
        return null;
    }

    public float particleHeight(BlockPos pos, BlockState bs, World world){
        return 0.75f;
    }


}
