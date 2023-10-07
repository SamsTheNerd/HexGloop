package com.samsthenerd.hexgloop.casting.mirror;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.items.ItemHandMirror;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class SyncedItemHandling {
    // for nbt rendering stuff: 0 = no, 1 = yes from mirror, 2 = yes from pedestal
    public static final String IS_REFLECTED_TAG = "is_reflected";

    // null if no alternate
    @Nullable
    public static ItemStack getAlternateHandStack(LivingEntity livEnt, Hand hand, @Nullable CastingContext context){
        ItemEntity itemEnt = getAlternateEntity(livEnt, hand, context);
        if(itemEnt != null){
            return itemEnt.getStack();
        }
        return null;
    }

    // if context is null then we just don't check ambit - important for getting it for arbitrary rendering stuff
    @Nullable
    public static ItemEntity getAlternateEntity(LivingEntity livEnt, Hand hand, @Nullable CastingContext context){
        ItemStack originalStack = livEnt.getStackInHand(hand);
        if(originalStack.getItem() instanceof ItemHandMirror mirrorItem && livEnt.getWorld() instanceof ServerWorld sWorld){
            RegistryKey<World> itemDim = mirrorItem.getMirroredItemDimension(originalStack);
            if(itemDim == null) itemDim = sWorld.getRegistryKey();
            ServerWorld itemWorld = sWorld.getServer().getWorld(itemDim);
            Iota iota = mirrorItem.readIota(originalStack, itemWorld);
            if(iota instanceof EntityIota entIota && entIota.getEntity() instanceof ItemEntity itemEnt){
                return itemEnt;
            }
        }
        if(originalStack.isEmpty()){
            // try to get the bound stack
            IMirrorBinder binder = null;
            // implicit null check and easy safe cast
            if(((Object)context) instanceof IMirrorBinder mirrorContext){
                binder = mirrorContext;
            } else if(livEnt instanceof IMirrorBinder mirrorEnt){ // same but for the maybe player
                binder = mirrorEnt;
            }
            if(binder == null) return null;
            BoundMirror boundMirror = binder.getBoundMirror();
            if(boundMirror != null){
                ItemEntity itemEnt = boundMirror.getItemEntity(livEnt.getServer());
                return itemEnt;
            }
        }
        // handle player specific bound stuff down here i guess
        return null;
    }

    // boolean is if it's from a hand or from a bound pedestal. null if there's just nothing relevant there
    // if you call this on both hands, if there is a bound pedestal it will only return the item for a single hand, with preference for the offhand
    @Nullable
    public static Pair<ItemStack, Boolean> getAlternateStackClient(LivingEntity ent, Hand hand){
        // handle hand mirror first
        ItemStack originalStack = ent.getStackInHand(hand);
        if(originalStack.getItem() instanceof ItemHandMirror mirrorItem){
            ItemStack mirrorStack = mirrorItem.getMirroredItemStack(originalStack);
            if(!mirrorStack.isEmpty() && mirrorItem.isMirrorActivated(originalStack)) return new Pair<>(mirrorStack, true);
        }
        // handle pedestal thing here later i guess
        if(originalStack.isEmpty()){
            // go ahead for offhand then only use main hand if there's something in the offhand
            if(hand == Hand.OFF_HAND || !ent.getStackInHand(Hand.OFF_HAND).isEmpty()){
                // try to get the bound stack
                // implicit null check and easy safe cast
                if(ent instanceof IShallowMirrorBinder mirrorEnt){
                    ItemStack trackedStack = mirrorEnt.getTrackedStack();
                    if(!trackedStack.isEmpty()) return new Pair<>(trackedStack, false);
                }
            }
        }
        return null;
    }
}
