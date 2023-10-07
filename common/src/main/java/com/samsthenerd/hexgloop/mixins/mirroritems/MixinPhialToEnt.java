package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(targets = "at/petrak/hexcasting/common/casting/operators/spells/OpMakeBattery$Spell")
public class MixinPhialToEnt {
    @WrapOperation(method = "cast(Lat/petrak/hexcasting/api/spell/casting/CastingContext;)V",
    at=@At(value="INVOKE", target="net/minecraft/server/network/ServerPlayerEntity.setStackInHand (Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;)V"))
    public void redirectSetHandStackPhial(ServerPlayerEntity player, Hand hand, ItemStack stack, Operation<?> original, CastingContext context){
        ItemStack altStack = SyncedItemHandling.getAlternateHandStack(player, hand, context);
        if(altStack != null){
            ItemEntity ent = SyncedItemHandling.getAlternateEntity(player, hand, context);
            if(ent != null){
                // probably fine ?
                ent.setStack(stack);
                return;
            }
        }
        original.call(player, hand, stack);
    }
}
