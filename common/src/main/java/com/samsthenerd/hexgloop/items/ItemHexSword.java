package com.samsthenerd.hexgloop.items;

import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;

// come back to this
public class ItemHexSword extends SwordItem {//implements HexHolderItem{
    public ItemHexSword(int attackDamage, float attackSpeed, Item.Settings settings) {
        super(ToolMaterials.NETHERITE, attackDamage, attackSpeed, settings);
    }
}
