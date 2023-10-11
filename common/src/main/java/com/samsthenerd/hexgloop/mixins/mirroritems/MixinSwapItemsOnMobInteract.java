package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough.SimplePTUContext;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

@Mixin(MobEntity.class)
public class MixinSwapItemsOnMobInteract {
    @WrapOperation(method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
    at=@At(value="INVOKE", target="net/minecraft/entity/mob/MobEntity.interactWithItem (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult swapItemsOnInteractWithItem(MobEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original){
        return wrapInteract(mob, player, hand, original);
    }

    @WrapOperation(method="interact(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
    at=@At(value="INVOKE", target="net/minecraft/entity/mob/MobEntity.interactMob (Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;"))
    public ActionResult swapItemsOnInteractMob(MobEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original){
        return wrapInteract(mob, player, hand, original);
    }

    // they're all the same signatures, don't think i can target multiple locations 
    private static ActionResult wrapInteract(MobEntity mob, PlayerEntity player, Hand hand, Operation<ActionResult> original){
        ItemStack heldItem = player.getStackInHand(hand);
        // keep this up here to just exit early if we need to -- although it seems like 
        if(!(heldItem.getItem() instanceof ItemAbstractPassThrough passThroughItem)){
            return original.call(mob, player, hand);
        }
        SimplePTUContext<ActionResult> useContext = new SimplePTUContext<>(player.getWorld(), player, hand, passThroughItem, (ctx)->{
            ActionResult result = original.call(mob, player, hand);
            ItemStack newStackToStore = player.getStackInHand(hand);
            if(newStackToStore != ctx.storedItemRef) newStackToStore = newStackToStore.copy(); // copy incase it's somehow getting cleared elsewhere or something ?
            ctx.storedItemRef = newStackToStore;
            return new Pair<>(result, ctx.storedItemRef);
        });
        ActionResult result = useContext.call();
        return useContext.didSucceed ? result : original.call(mob, player, hand);
    }
}
