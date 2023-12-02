package com.samsthenerd.hexgloop.blocks;

import com.samsthenerd.hexgloop.utils.ClientUtils;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BlockEnlightenedCollision extends TransparentBlock{
    public final boolean passByDefault;
    public BlockEnlightenedCollision(Settings settings, boolean passByDefault) {
        super(settings);
        this.passByDefault = passByDefault;
    }

    // copied from glass block
    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean isEnlightened = false;
        if(context instanceof EntityShapeContext entityShapeContext && entityShapeContext.getEntity() instanceof PlayerEntity player){
            if(player instanceof ServerPlayerEntity sPlayer){
                // server
                Advancement adv = sPlayer.getServer().getAdvancementLoader().get(HexAPI.modLoc("enlightenment"));
                PlayerAdvancementTracker advs = sPlayer.getAdvancementTracker();
                if(advs.getProgress(adv) != null)
                    isEnlightened = advs.getProgress(adv).isDone();
            } else {
                // client - probably important because y'know, movement
                isEnlightened = ClientUtils.isEnlightened();
            }
        }
        return isEnlightened ^ passByDefault ? VoxelShapes.empty() : state.getOutlineShape(world, pos);
    }
}
