package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockAccelerator extends BlockCircleComponent implements ILociAtHome{
    public static final DirectionProperty FACING = Properties.FACING;

    public BlockAccelerator(Settings settings) {
        super(settings);
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(FACING, Direction.NORTH));
    }

    public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, VoxelShapes.cuboid(0, 0, 0.25, 1, 1, 1));
        SHAPES.put(Direction.SOUTH, VoxelShapes.cuboid(0, 0, 0, 1, 1, 0.75));
        SHAPES.put(Direction.EAST, VoxelShapes.cuboid(0, 0, 0, 0.75, 1, 1));
        SHAPES.put(Direction.WEST, VoxelShapes.cuboid(0.25, 0, 0, 1, 1, 1));
        SHAPES.put(Direction.UP, VoxelShapes.cuboid(0, 0, 0, 1, 0.75, 1));
        SHAPES.put(Direction.DOWN, VoxelShapes.cuboid(0, 0.25, 0, 1, 1, 1));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES.get(state.get(FACING));
    }

    @Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(FACING, ctx.getPlayerLookDirection().getOpposite());
	}

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(ENERGIZED) ? 15 : 0;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType( @Nonnull BlockState state )
    {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
    }

    // circle-y stuff
    @Override
    public boolean canEnterFromDirection(Direction enterDir, Direction normalDir, BlockPos pos,
        BlockState bs, World world){
        var thisNormal = this.normalDir(pos, bs, world);
        return enterDir != thisNormal && normalDir == thisNormal;
    }

    // yoinked from BlockSlate
    public EnumSet<Direction> exitDirections(BlockPos pos, BlockState bs, World world){
        var allDirs = EnumSet.allOf(Direction.class);
        var normal = this.normalDir(pos, bs, world);
        allDirs.remove(normal);
        allDirs.remove(normal.getOpposite());
        return allDirs;
    }

    @Nullable
    public HexPattern getPattern(BlockPos pos, BlockState bs, World world){
        return null;
    }

    public void rawLociCall(BlockPos pos, BlockState bs, World world, CastingHarness harness){
        // don't actually need to do anything
    }

    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft){
        return bs.get(FACING);
    }

    public float particleHeight(BlockPos pos, BlockState bs, World world){
        return (float)BlockEntityPedestal.HEIGHT;
    }

    // accelerate !
    public double modifyTickDelay(int blocksAgo, double currentModifier, int originalSpeed, 
        int similarBlockCount, List<BlockPos> trackedBlocks, World world, BlockEntityAbstractImpetus impetus){
        return 0.5; // can adjust this
    }
}
