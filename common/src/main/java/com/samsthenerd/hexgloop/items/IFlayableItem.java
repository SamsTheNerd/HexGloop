package com.samsthenerd.hexgloop.items;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;

// an item that can act as a substitute for a villager
public interface IFlayableItem {
    // doesn't need to be an actually spawned in-world villager, you can just make the entity and return it.
    // return null if there's no villager to flay
    @Nullable
    public VillagerEntity getFlayableVillager(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx);

    // do whatever, probably just decrement the stack count or clear some nbt
    public void handleBrainsweep(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx);

    // if it counts as murder - if so it plays the death sound and alerts villagers
    public default boolean wasMurderous(ItemStack stack, @Nullable ItemEntity itemEnt, CastingContext ctx){
        return false;
    }
}
