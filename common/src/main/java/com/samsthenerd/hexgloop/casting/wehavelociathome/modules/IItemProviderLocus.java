package com.samsthenerd.hexgloop.casting.wehavelociathome.modules;

import at.petrak.hexcasting.api.block.circle.BlockEntityAbstractImpetus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemProviderLocus extends ILocusModule {
    public DefaultedList<ItemStack> getProvidedItems(BlockPos pos, World world, BlockEntityAbstractImpetus impetus);
}
