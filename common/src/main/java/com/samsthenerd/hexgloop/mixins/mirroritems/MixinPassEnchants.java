package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.block.BlockState;
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

    // also do suitability check, don't feel like making a new mixin
    @Inject(method="isSuitableFor(Lnet/minecraft/block/BlockState;)Z", 
    at=@At(value="HEAD"), cancellable=true)
    public void reflectSuitability(BlockState state, CallbackInfoReturnable<Boolean> original){
        ItemStack stack = (ItemStack)(Object)this;
        if(stack.getItem() instanceof ItemAbstractPassThrough passItem && passItem.shouldPassTools(stack)){
            ItemStack storedItem = passItem.getStoredItemCopy(stack);
            if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
                original.setReturnValue(storedItem.isSuitableFor(state));
            }
        }
    }
}
