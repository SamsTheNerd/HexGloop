package com.samsthenerd.hexgloop.casting;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.items.ItemHandMirror;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class SyncedItemHandling {
    // null if no alternate
    @Nullable
    public static ItemStack getAlternateHandStack(ServerPlayerEntity player, Hand hand, CastingContext context){
        ItemStack originalStack = player.getStackInHand(hand);
        if(originalStack.getItem() instanceof ItemHandMirror mirrorItem){
            Iota iota = mirrorItem.readIota(originalStack, context.getWorld());
            if(iota instanceof EntityIota entIota && entIota.getEntity() instanceof ItemEntity itemEnt){
                return itemEnt.getStack();
            }
        }
        return null;
    }
}
