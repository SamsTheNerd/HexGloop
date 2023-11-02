package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;
import com.samsthenerd.hexgloop.casting.wehavelociathome.LociUtils;
import com.samsthenerd.hexgloop.casting.wehavelociathome.modules.ISpeedLocus;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import at.petrak.hexcasting.api.misc.MediaConstants;
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

public class BlockAccelerator extends BlockCircleComponent implements ILociAtHome, ISpeedLocus{
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
    @Override
    public double modifyTickDelay(int blocksAgo, double currentModifier, int originalSpeed, 
        int similarBlockCount, List<BlockPos> trackedBlocks, World world, BlockEntityAbstractImpetus impetus){
        
        if(blocksAgo > 10) return 1;
        double currentSpeed = 1 / currentModifier;
        double speedIncrease = Math.max((10 - blocksAgo) * 0.2, 0);
        if(blocksAgo == 0){
            // handle cost - 1 dust * newSpeed-1 + 1 shard for every whole 100% over 3x
            double costDust = MediaConstants.DUST_UNIT * (currentSpeed + speedIncrease - 1);
            double costShard = Math.floor(currentSpeed + speedIncrease - 3) * MediaConstants.SHARD_UNIT;
            double cost = costDust + costShard; // every 100% over 3x costs a shard

            // HexGloop.logPrint("entered accelerator #" + similarBlockCount + " with: " +
            //     "\n\tentering speed of " + currentSpeed + "x" +
            //     "\n\tincrease of " + speedIncrease + "x" + 
            //     "\n\texiting with speed: " + currentSpeed + speedIncrease + "x" + 
            //     "\n\tcost: " + costDust + " + " + costShard + " = " + cost + " media");
            
            if(!LociUtils.withdrawCircleMedia(impetus, (int)Math.min(cost, LociUtils.getCircleMedia(impetus)))){
                // just let it go, no good way to track if it had enough
            }
            return -1; // instant activate the next block
        }
        return ISpeedLocus.modifierForTarget(speedIncrease+currentSpeed, currentModifier);
    }
}
