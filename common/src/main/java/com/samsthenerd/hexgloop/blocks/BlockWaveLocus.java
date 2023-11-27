package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;

import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockWaveLocus extends BlockCircleComponent implements ILociAtHome{

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public BlockWaveLocus(Settings settings) {
        super(settings);
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(POWERED, false)
        );
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
        pBuilder.add(POWERED);
    }


    public void waveEnter(BlockPos pos, BlockState bs, World world, BlockEntityAbstractImpetus impetus){
        world.setBlockState(pos, bs.with(POWERED, true));
        world.updateNeighborsAlways(pos, this);
    }

    public void waveExit(BlockPos pos, BlockState bs, World world, BlockEntityAbstractImpetus impetus){
        world.setBlockState(pos, bs.with(POWERED, false));
        world.updateNeighborsAlways(pos, this);
    }

    // called when the circle stops, either early or on time.
    public void circleStopped(BlockPos pos, BlockState bs, World world, BlockEntityAbstractImpetus impetus){
        world.setBlockState(pos, bs.with(POWERED, false));
        world.updateNeighborsAlways(pos, this);
    }

    // not sure if this should be true only when powered ?
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }
}
