package com.samsthenerd.hexgloop.blocks;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blockentities.BlockEntityDial;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockIoticDial extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.FACING;
    public static final IntProperty SELECTED = IntProperty.of("selected", 0, 6);

    public BlockIoticDial(Settings properties) {
        super(properties);
        setDefaultState(this.stateManager.getDefaultState()
            .with(FACING, Direction.NORTH)
            .with(SELECTED, 0)
        );
    }

    public static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.NORTH, VoxelShapes.cuboid(0, 0, 11.0/16.0, 1, 1, 1));
        SHAPES.put(Direction.SOUTH, VoxelShapes.cuboid(0, 0, 0, 1, 1, 5.0/16.0));
        SHAPES.put(Direction.EAST, VoxelShapes.cuboid(0, 0, 0, 5.0/16.0, 1, 1));
        SHAPES.put(Direction.WEST, VoxelShapes.cuboid(11.0/16.0, 0, 0, 1, 1, 1));
        SHAPES.put(Direction.UP, VoxelShapes.cuboid(0, 0, 0, 1, 5.0/16.0, 1));
        SHAPES.put(Direction.DOWN, VoxelShapes.cuboid(0, 11.0/16.0, 0, 1, 1, 1));
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state){
        return new BlockEntityDial(pos, state);
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING);
        builder.add(SELECTED);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return state.get(SELECTED);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderType( @Nonnull BlockState state )
    {
        return BlockRenderType.MODEL;
    }

    private static Map<Direction.Axis, FaceCalcinator> faceCalcs = new HashMap<Direction.Axis, FaceCalcinator>();
    
    static {
        faceCalcs.put(Direction.Axis.X, (mirrored, pos) -> mirrored ? new Pair<>(pos.z, pos.y) : new Pair<>(1-pos.z, pos.y));
        faceCalcs.put(Direction.Axis.Y, (mirrored, pos) -> mirrored ? new Pair<>(pos.x, pos.z) : new Pair<>(pos.x, 1-pos.z));
        faceCalcs.put(Direction.Axis.Z, (mirrored, pos) -> mirrored ? new Pair<>(1-pos.x, pos.y) : new Pair<>(pos.x, pos.y));
    };

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!(world.getBlockEntity(pos) instanceof BlockEntityDial dialBE)) return ActionResult.FAIL;
        if(dialBE.getInnerMultiFocus().isEmpty() && player.getStackInHand(hand).getItem() == HexGloopItems.MULTI_FOCUS_ITEM.get()){
            dialBE.setInnerMultiFocus(player.getStackInHand(hand).copy());
            player.getStackInHand(hand).decrement(1);
            return ActionResult.SUCCESS;
        }
        if(!dialBE.getInnerMultiFocus().isEmpty() && player.isSneaking()){
            player.getInventory().offerOrDrop(dialBE.getInnerMultiFocus());
            dialBE.setInnerMultiFocus(ItemStack.EMPTY);
            return ActionResult.SUCCESS;
        }
        if(dialBE.getInnerMultiFocus().isEmpty()) return ActionResult.FAIL;
        Direction face = hit.getSide();
        Vec3d nPos = hit.getPos().subtract(pos.getX(), pos.getY(), pos.getZ());
        Pair<Double, Double> coords = faceCalcs.get(face.getAxis()).calc(face.getDirection() == Direction.AxisDirection.NEGATIVE, nPos);
        double r = new Vec3d(coords.getLeft()-0.5, coords.getRight()-0.5, 0).length();
        double angle = (Math.atan2(coords.getRight()-0.5, coords.getLeft()-0.5) + (2 * Math.PI)) % (2 * Math.PI);
        if(face == state.get(FACING)){
            int sel = (1 + 6 - (int)Math.floor(6 * angle / (2 * Math.PI))) % 6 + 1;
            dialBE.setSelection(sel);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
        // HexGloop.logPrint("(" + coords.getLeft() + ", " + coords.getRight() + ")");
    }

    @FunctionalInterface
    public static interface FaceCalcinator{
        public Pair<Double, Double> calc(boolean mirrored, Vec3d pos);
    }
}
