package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;


@Mixin(ItemStack.class)
public class MixinPassEnchants {
    @Inject(method="getEnchantments()Lnet/minecraft/nbt/NbtList;", at=@At("HEAD"), cancellable=true)
    public void reflectEnchantments(CallbackInfoReturnable<NbtList> cir){
        ItemStack stack = (ItemStack)(Object)this;
        if(stack.getItem() instanceof ItemAbstractPassThrough passItem && passItem.shouldPassTools(stack)){
            ItemStack storedItem = passItem.getStoredItemCopy(stack);
            if(storedItem != null){
                cir.setReturnValue(storedItem.getEnchantments());
            }
        }
    }
}
