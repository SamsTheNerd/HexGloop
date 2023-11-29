package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class BlockSlateLamp extends BlockCircleComponent {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public BlockSlateLamp(Settings settings) {
        super(settings);
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(LIT, false)
        );
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(LIT, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        boolean bl = state.get(LIT);
        if (bl != world.isReceivingRedstonePower(pos)) {
            if (bl) {
                world.createAndScheduleBlockTick(pos, this, 4);
            } else {
                world.setBlockState(pos, (BlockState)state.cycle(LIT), Block.NOTIFY_LISTENERS);
            }
        }
    }

    // idk why it's saying it can't override ?
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(LIT).booleanValue() && !world.isReceivingRedstonePower(pos)) {
            world.setBlockState(pos, (BlockState)state.cycle(LIT), Block.NOTIFY_LISTENERS);
        }
    }

    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos,
        BlockState bs, World world){
        return true;
    }

    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world){
        return EnumSet.allOf(Direction.class);
    }

    // it's not really a directional block
    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft){
        return Direction.UP;
    }

    public HexPattern getPattern(BlockPos pos, BlockState bs, World world){
        return null;
    }

    public float particleHeight(BlockPos pos, BlockState bs, World world){
        return 0.6f;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> pBuilder) {
        super.appendProperties(pBuilder);
        pBuilder.add(LIT);
    }
}
