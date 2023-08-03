package com.samsthenerd.hexgloop.mixins.wnboi;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.items.LabelyItem;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

@Mixin(ItemSpellbook.class)
public class MixinLabelySpellbook implements LabelyItem{
    @Nullable
    public boolean putLabel(ItemStack stack, NbtCompound labelNbt){
        int index = ItemSpellbook.getPage(stack, 0);
        return putLabel(stack, index, labelNbt);
    }
}
