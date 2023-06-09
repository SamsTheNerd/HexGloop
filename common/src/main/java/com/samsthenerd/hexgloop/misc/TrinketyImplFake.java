package com.samsthenerd.hexgloop.misc;

import net.minecraft.entity.LivingEntity;

// we want trinket-y mods to be optional, so add this interface to have something to throw in
public class TrinketyImplFake implements ITrinkety{
    public TrinketyImplFake(){

    }

    @Override
    public boolean isCastingRingEquipped(LivingEntity player){
        return false;
    }
}
