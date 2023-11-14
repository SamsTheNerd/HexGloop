package com.samsthenerd.hexgloop.blocks;

import java.util.Optional;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.blockentities.BlockEntityConjuredRedstone;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;

import at.petrak.hexcasting.annotations.SoftImplement;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.blocks.BlockConjured;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BlockConjuredRedstone extends BlockConjured{

    public BlockConjuredRedstone(Settings properties) {
        super(properties);
    }

    @Override
    public @NotNull BlockRenderType getRenderType(@NotNull BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        Optional<BlockEntityConjuredRedstone> maybeBE = world.getBlockEntity(pos, HexGloopBEs.CONJURED_REDSTONE_BE.get());
        if(maybeBE.isPresent()){
            return maybeBE.get().getPower();
        }
        return 0;
    }

    // yoinked from BlockConjured -- need to override stuff to use our BE

    @Override
    public void onBreak(World pLevel, BlockPos pPos, BlockState pState, PlayerEntity pPlayer) {
        super.onBreak(pLevel, pPos, pState, pPlayer);
        // For some reason the block doesn't play breaking noises. So we fix that!
        pPlayer.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, 1f, 1f);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World pLevel, BlockState pState,
        BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClient()) {
            return BlockConjuredRedstone::tick;
        } else {
            return null;
        }
    }

    private static <T extends BlockEntity> void tick(World level, BlockPos blockPos, BlockState blockState, T t) {
        if (t instanceof BlockEntityConjuredRedstone conjured) {
            conjured.particleEffect();
        }
    }

    @Override
    public void onSteppedOn(World pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Entity pEntity) {
        BlockEntity tile = pLevel.getBlockEntity(pPos);
        if (tile instanceof BlockEntityConjuredRedstone bec) {
            bec.walkParticle(pEntity);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new BlockEntityConjuredRedstone(pPos, pState);
    }

    public static void setColor(World world, BlockPos pPos, FrozenColorizer colorizer) {
        BlockEntity blockentity = world.getBlockEntity(pPos);
        if (blockentity instanceof BlockEntityConjuredRedstone tile) {
            tile.setColorizer(colorizer);
        }
    }

    public static void setPower(World world, BlockPos pPos, int power) {
        BlockEntity blockentity = world.getBlockEntity(pPos);
        if (blockentity instanceof BlockEntityConjuredRedstone tile) {
            tile.setPower(power);
            world.updateNeighborsAlways(pPos, world.getBlockState(pPos).getBlock());
        }
    }

    @SoftImplement("forge")
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2,
                                     LivingEntity entity, int numberOfParticles) {
        return addLandingEffects(state1, worldserver, pos, entity, numberOfParticles);
    }

    @Override
    public boolean addLandingEffects(BlockState state, ServerWorld worldserver, BlockPos pos,
                                     LivingEntity entity, int numberOfParticles) {
        BlockEntity tile = worldserver.getBlockEntity(pos);
        if (tile instanceof BlockEntityConjuredRedstone bec) {
            bec.landParticle(entity, numberOfParticles);
        }
        return true;
    }
}