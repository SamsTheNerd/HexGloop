package com.samsthenerd.hexgloop.items;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface LabelyItem {
    @Nullable
    public default boolean putLabel(ItemStack stack, int index, NbtCompound labelNbt){
        return LabelMaker.putLabel(stack, labelNbt, index);
    }

    // put label to the current selection
    @Nullable
    public boolean putLabel(ItemStack stack, NbtCompound labelNbt);
}
