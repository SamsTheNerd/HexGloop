package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ChestBlockEntity.class)
public interface MixinExposeChestInventory {
    @Accessor("inventory")
    public void setInventory(DefaultedList<ItemStack> inv);
}
