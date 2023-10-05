package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;

public class ItemDyeableSpellbook extends ItemSpellbook implements DyeableItem{
    public ItemDyeableSpellbook(Settings properties) {
        super(properties);
    }
    
    // silly silly
    @Override
    public int getColor(ItemStack stack){
        return DyeableItem.super.getColor(stack);
    }

    // gets the iota color
    public int getIotaColor(ItemStack stack){
        return super.getColor(stack);
    }
}
