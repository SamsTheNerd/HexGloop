package com.samsthenerd.hexgloop.misc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface ITrinkety {
    // maps different implementation ids into standardized ones
    public static final Map<String, String> standardizedSlots = new HashMap<String, String>(){
        {
            put("necklace", "necklace");
            put("chest/necklace", "necklace");
            put("offhand/ring", "offhandring");
            put("hand/ring", "mainhandring");
            put("ring", "mainhandring");
        }
    };
    
    public boolean isCastingRingEquipped(LivingEntity player);

    // returns a list of all trinkets and 
    @Nonnull
    public Map<String, List<ItemStack>> getTrinkets(LivingEntity player);
}
