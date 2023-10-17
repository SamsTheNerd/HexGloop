package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.IExtendedEnchantable;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;


@Mixin(targets = {
    "net/minecraft/enchantment/EnchantmentTarget$1",
    "net/minecraft/enchantment/EnchantmentTarget$2",
    "net/minecraft/enchantment/EnchantmentTarget$3",
    "net/minecraft/enchantment/EnchantmentTarget$4",
    "net/minecraft/enchantment/EnchantmentTarget$5",
    "net/minecraft/enchantment/EnchantmentTarget$6",
    "net/minecraft/enchantment/EnchantmentTarget$7",
    "net/minecraft/enchantment/EnchantmentTarget$8",
    "net/minecraft/enchantment/EnchantmentTarget$9",
    "net/minecraft/enchantment/EnchantmentTarget$10",
    "net/minecraft/enchantment/EnchantmentTarget$11",
    "net/minecraft/enchantment/EnchantmentTarget$12",
    "net/minecraft/enchantment/EnchantmentTarget$13",
    "net/minecraft/enchantment/EnchantmentTarget$14",
})
public class MixinInterfaceEnchants {
    @Inject(method="isAcceptableItem(Lnet/minecraft/item/Item;)Z", at=@At("RETURN"), cancellable = true)
    public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue()){
            if(item instanceof IExtendedEnchantable enchantItem){
                EnchantmentTarget enchantTarget = (EnchantmentTarget)(Object)this;
                cir.setReturnValue(enchantItem.canAcceptEnchantment(enchantTarget, item));
            }
        }
    }
}
