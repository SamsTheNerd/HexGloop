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
public class MixinMaintainUsingCorrectItem {

    @Shadow
    protected ItemStack activeItemStack;

    // @Inject(method="tickActiveItemStack()V",
    // at=@At("HEAD"))
    // private void seeWhatsHappeningInActiveItemStack(CallbackInfo ci){
    //     LivingEntity thisEnt = (LivingEntity)(Object)this;
    //     if(!(thisEnt instanceof PlayerEntity)) return;
    //     ItemStack handStack = thisEnt.getStackInHand(thisEnt.getActiveHand());
    //     // HexGloop.logPrint("current item tick:\n\tactiveItemStack: " + activeItemStack + "\n\thandStack: " + handStack);
    // }

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

    @WrapOperation(method="consumeItem()V",
    at=@At(value="INVOKE", target="java/lang/Object.equals (Ljava/lang/Object;)Z"))
    private boolean makeItemsEqualInConsume(Object left, Object right, Operation<Boolean> original){
        // left is active, right is hand
        // HexGloop.logPrint("entered consume mixin I");
        boolean originalRes = original.call(left, right);
        if(originalRes) return true;
        if(left instanceof ItemStack leftStack && right instanceof ItemStack rightStack){
            if(rightStack.getItem() instanceof ItemAbstractPassThrough passItem){
                LivingEntity thisEnt = (LivingEntity)(Object)this;
                ItemStack storedItem = passItem.getStoredItem(rightStack, thisEnt, thisEnt.getWorld(), thisEnt.getActiveHand());
                // HexGloop.logPrint("in consume mixin: stored item: [" + storedItem.toString() + "] vs handstack: [" + leftStack.toString() + "]");
                if(storedItem != null) 
                    return original.call(leftStack, storedItem);
            }
        }
        return originalRes;
    }
}
