package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.SyncedItemHandling;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(CastingContext.class)
public class MixinDirectlyOnGetHeldItem {
    @WrapOperation(method = "getHeldItemToOperateOn(Lkotlin/jvm/functions/Function1;)Lkotlin/Pair;",
    at = @At(value = "INVOKE", target="net/minecraft/server/network/ServerPlayerEntity.getStackInHand (Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack getAlternateHandStack(ServerPlayerEntity player, Hand hand, Operation<ItemStack> original){
        ItemStack altStack = SyncedItemHandling.getAlternateHandStack(player, hand, (CastingContext)(Object)this);
        return altStack == null ? original.call(player, hand) : altStack;
    }
}
