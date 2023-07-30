package com.samsthenerd.hexgloop.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

// we want trinket-y mods to be optional, so add this interface to have something to throw in
public class TrinketyImplFake implements ITrinkety{
    public TrinketyImplFake(){

    }

    public Map<String, List<ItemStack>> getTrinkets(LivingEntity player){
        return new HashMap<String, List<ItemStack>>();
    }

    public boolean isCastingRingEquipped(LivingEntity player){
        return false;
    }

    public List<ItemStack> getMainRing(LivingEntity player){
        return new ArrayList<ItemStack>();
    }

    public List<ItemStack> getOffRing(LivingEntity player){
        return new ArrayList<ItemStack>();
    }

    public List<ItemStack> getNecklace(LivingEntity player){
        return new ArrayList<ItemStack>();
    }
}
