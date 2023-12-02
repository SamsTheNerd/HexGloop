package com.samsthenerd.hexgloop.items;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;

// an item that can accept a mind - meant to be used with the pedestal mostly
// see also: IFlayableItem for items that can *provide* a mind
public interface IMindTargetItem {
    // politely ask you to not modify the sacrifice
    // return resulting itemstack - will keep existing on the pedestal and spawn the new one if it doesn't match the one passed in.
    public ItemStack absorbVillagerMind(VillagerEntity sacrifice, ItemStack stack, @Nullable CastingContext ctx);
    
    // return true if it can be accepted
    public boolean canAcceptMind(VillagerEntity sacrifice, ItemStack stack, @Nullable CastingContext ctx);

    // determines whether or not it'll suck the mind out of a flaying pedestal
    public default boolean shouldPullMind(VillagerEntity sacrifice, ItemStack stack, @Nullable CastingContext ctx){
        return false;
    }

    public static String STORED_MIND_TAG = "stored_mind";
}
