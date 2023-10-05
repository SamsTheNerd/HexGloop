package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

@Mixin(PlayerInventory.class)
public class MixinRemoveFromPlayerInventory {

    @Shadow
    @Final
    public PlayerEntity player;

    @Shadow
    public int selectedSlot;

    @WrapOperation(method="removeOne(Lnet/minecraft/item/ItemStack;)V",
    at=@At(value="INVOKE", target="net/minecraft/util/collection/DefaultedList.get (I)Ljava/lang/Object;"))
    private Object removeMirrorItems(DefaultedList defaultedList, int index, Operation<Object> original, ItemStack stackToRemove){
        Object objectAtSlot = original.call(defaultedList, index);
        if(!(objectAtSlot instanceof ItemStack stackAtSlot)) return objectAtSlot;
        if(stackAtSlot.isEmpty()) return stackAtSlot;
        // maybe add more checks in here so it grabs stuff for like more offhand mods ?
        Hand hand = null;
        if(stackAtSlot == player.getMainHandStack()) hand = Hand.MAIN_HAND;
        if(stackAtSlot == player.getOffHandStack()) hand = Hand.OFF_HAND;
        if(stackAtSlot.getItem() instanceof ItemAbstractPassThrough passItem && hand != null){
            ItemStack storedItem = passItem.getStoredItem(stackAtSlot, player, player.world, hand);
            if(storedItem != null && storedItem == stackToRemove){
                passItem.setStoredItem(stackAtSlot, player, player.getWorld(), hand, ItemStack.EMPTY);
            }
        }
        return stackAtSlot;
    }
}
