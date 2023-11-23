package com.samsthenerd.hexgloop.blocks;

import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BlockSentinelBed extends Block implements ICatPost{
    public BlockSentinelBed (Settings settings){
        super(settings);
    }

    public static Modification ambitModifier(CastingContext ctx, Vec3d pos, Boolean original){
        ServerPlayerEntity caster = ctx.getCaster();
        if(!original && caster != null){
            BlockState state = caster.getWorld().getBlockState(new BlockPos(pos));
            if(state.getBlock() == HexGloopBlocks.SENTINEL_BED_BLOCK.get())
                return Modification.ENABLE;
        }
        return Modification.NONE;
    }
}
