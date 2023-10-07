package com.samsthenerd.hexgloop.casting.mirror;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface IShallowMirrorBinder {

    // put it out here so we can use it elsewhere :)
    public static final TrackedData<ItemStack> HELD_STACK = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
     // should only really be used for rendering
    public ItemStack getTrackedStack();
}
