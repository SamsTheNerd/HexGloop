package com.samsthenerd.hexgloop.mixins.canvas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(ItemFrameEntity.class)
public class MixinClientItemFrameCanvas {
    @WrapOperation(
        method="getMapId()Ljava/util/OptionalInt;",
        at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z")
    )
    public boolean canvasIsBasicallyAMapIThinkDontYouAgreeItemFrameIDFinder(ItemStack stack, Item item, Operation<Boolean> original){
        if(item == Items.FILLED_MAP && stack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())){
            return true;
        }
        return original.call(stack, item);
    }

    @WrapOperation(
        method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z")
    )
    public boolean canvasIsBasicallyAMapIThinkDontYouAgreeItemFrameInteractionMethod(ItemStack stack, Item item, Operation<Boolean> original){
        if(item == Items.FILLED_MAP && stack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())){
            return true;
        }
        return original.call(stack, item);
    }


}
