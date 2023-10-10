package com.samsthenerd.hexgloop.items;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;


public interface IExtendedEnchantable {
    boolean canAcceptEnchantment(EnchantmentTarget target, Item item);

    // some default implementations for convenience, don't use these if you need multiple, just implement the generic interface
    public static interface IWeaponEnchantable extends IExtendedEnchantable{
        default boolean canAcceptEnchantment(EnchantmentTarget target, Item item){
            return target == EnchantmentTarget.WEAPON;
        }
    }

    public static interface IToolEnchantable extends IExtendedEnchantable{
        default boolean canAcceptEnchantment(EnchantmentTarget target, Item item){
            return target == EnchantmentTarget.DIGGER;
        }
    }
}
