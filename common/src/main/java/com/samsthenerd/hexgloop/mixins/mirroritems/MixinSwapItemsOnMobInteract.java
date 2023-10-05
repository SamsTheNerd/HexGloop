package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Mixin(MobEntity.class)
public class MixinSwapItemsOnMobInteract {
    @WrapOperation(method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
    at=@At(value="INVOKE", target="net/minecraft/entity/mob/MobEntity.interactWithItem (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult swapItemsOnInteractWithItem(MobEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original){
        return wrapInteract(mob, player, hand, original, true);
    }

    @WrapOperation(method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
    at=@At(value="INVOKE", target="net/minecraft/entity/mob/MobEntity.interactMob (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult swapItemsOnInteractMob(MobEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original){
        return wrapInteract(mob, player, hand, original, true);
    }

    // @WrapOperation(method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
    // at=@At(value="INVOKE", target="net/minecraft/entity/LivingEntity.interact (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    // public ActionResult swapItemsOnSuperInteract(LivingEntity livEnt, PlayerEntity player, Hand hand, Operation<ActionResult> original){
    //     return wrapInteract(livEnt, player, hand, original, false);
    // }



    // they're all the same signatures, don't think i can target multiple locations 
    private static ActionResult wrapInteract(LivingEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original, boolean isMob){
        ItemStack heldItem = player.getStackInHand(hand);
        if(!(heldItem.getItem() instanceof ItemAbstractPassThrough passThroughItem)){
            if(isMob && mob instanceof MobEntity forSureMob){
                return original.call(forSureMob, player, hand);
            } else {
                return original.call(mob, player, hand);
            }
        }
        ItemStack storedItem = passThroughItem.getStoredItem(heldItem, player, player.getWorld(), hand).copy();
        player.setStackInHand(hand, storedItem);
        ActionResult result;
        if(isMob && mob instanceof MobEntity forSureMob){
            result = original.call(forSureMob, player, hand);
        } else {
            result = original.call(mob, player, hand);
        }
        // HexGloop.logPrint("[" + (player.getWorld().isClient ? "client" : "server") + "]used item on entity, left with:\n\tstoredItem: " + storedItem.toString() + 
        //         "\n\tcurrentHand: " + player.getStackInHand(hand).toString());
        ItemStack newStackToStore = player.getStackInHand(hand);
        if(newStackToStore != storedItem) newStackToStore = newStackToStore.copy(); // copy incase it's somehow getting cleared elsewhere or something ?
        heldItem = passThroughItem.setStoredItem(heldItem, player, player.getWorld(), hand, newStackToStore);
        player.setStackInHand(hand, heldItem);
        return result;
    }
}
