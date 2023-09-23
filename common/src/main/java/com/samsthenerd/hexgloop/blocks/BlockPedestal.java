package com.samsthenerd.hexgloop.blocks;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociAtHome;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockPedestal extends BlockCircleComponent implements BlockEntityProvider, ILociAtHome{
    public final boolean isMirror;
    public static final DirectionProperty FACING = Properties.FACING;

    public BlockPedestal(Settings settings, boolean isMirror) {
        super(settings);
        this.isMirror = isMirror;
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(FACING, Direction.NORTH));
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
    
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(world.getBlockEntity(pos) instanceof BlockEntityPedestal be 
        // make sure it's not just changing something with the blockstate
        && newState.getBlock() != this){
            be.onRemoved();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
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
		return super.getPlacementState(ctx).with(FACING, ctx.getSide());
	}

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be == null) return 0;
        return ScreenHandler.calculateComparatorOutput((Inventory)be);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be != null){
            ActionResult res = be.use(player, hand, hit);
            if(res == ActionResult.SUCCESS){
                be.interacted(player);
            }
            return res;
        }
        return ActionResult.PASS;
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType( @Nonnull BlockState state )
    {
        return BlockRenderType.MODEL;
    }

    // from BlockWithEntity

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        super.onSyncedBlockEvent(state, world, pos, type, data);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }
        return blockEntity.onSyncedBlockEvent(type, data);
    }

    @Override
    @Nullable
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof NamedScreenHandlerFactory ? (NamedScreenHandlerFactory)((Object)blockEntity) : null;
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
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be != null) return be.getPattern();
        return null;
    }

    // for cool advanced stuff
    public void rawLociCall(BlockPos pos, BlockState bs, World world, CastingHarness harness){
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be != null) be.rawLociCall(harness);
    }

    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft){
        return bs.get(FACING);
    }

    public float particleHeight(BlockPos pos, BlockState bs, World world){
        return (float)BlockEntityPedestal.HEIGHT;
    }
}
