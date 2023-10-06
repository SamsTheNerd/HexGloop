package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Mixin(LivingEntity.class)
public class MixinMaintainUsingCorrectItemFabric {

    @Shadow
    protected ItemStack activeItemStack;

    @WrapOperation(method="tickActiveItemStack()V",
    at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.areItemsEqual (Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"))
    private boolean makeItemsEqualInTickActive(ItemStack left, ItemStack right, Operation<Boolean> original){
        // left is hand item, right is active item
        boolean originalRes = original.call(left, right);
        if(originalRes) return true;
        if(left.getItem() instanceof ItemAbstractPassThrough passItem){
            LivingEntity thisEnt = (LivingEntity)(Object)this;
            ItemStack storedItem = passItem.getStoredItem(left, thisEnt, thisEnt.getWorld(), thisEnt.getActiveHand());
            // HexGloop.logPrint("in tick active mixin: stored item: [" + (storedItem == null ? "null" : storedItem.toString()) + "] vs activeItemStack: [" + right.toString() + "]");
            if(storedItem == null) return original.call(left, right);
            return original.call(storedItem, right);
        }
        return originalRes;
    }
}
