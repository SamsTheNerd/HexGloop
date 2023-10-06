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

    // gets the iota color -- ok guess this doesn't work either. just hard crashes. just use the one in utils
    // this will be staying as a monument to our failures though
    // public int getIotaColor(ItemStack stack){
    //     return super.getColor(stack);
    // }
}
