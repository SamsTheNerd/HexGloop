package com.samsthenerd.hexgloop.items;

import com.samsthenerd.hexgloop.casting.wehavelociathome.ILociHandler;

import at.petrak.hexcasting.api.block.circle.BlockAbstractImpetus;
import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;


public class ItemCosmeticEnergizer extends Item {
    public ItemCosmeticEnergizer(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        if(state.getBlock() instanceof BlockCircleComponent){
            if(state.getBlock() instanceof BlockAbstractImpetus){

                if(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof BlockEntityAbstractImpetus impetus && impetus instanceof ILociHandler handler && !handler.getTrackedBlocks().isEmpty()){
                    return ActionResult.PASS;
                }
            }
            context.getWorld().setBlockState(context.getBlockPos(), state.with(BlockCircleComponent.ENERGIZED, !state.get(BlockCircleComponent.ENERGIZED)));
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}
