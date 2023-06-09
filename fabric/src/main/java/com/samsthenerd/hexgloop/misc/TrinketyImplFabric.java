package com.samsthenerd.hexgloop.misc;

import java.util.Optional;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;

public class TrinketyImplFabric implements ITrinkety{
    public TrinketyImplFabric(){

    }

    @Override
    public boolean isCastingRingEquipped(LivingEntity player){
        Optional<TrinketComponent> trinketComponentRes = TrinketsApi.getTrinketComponent(player);
        if(!trinketComponentRes.isPresent()) return false;
        TrinketComponent trinketComponent = trinketComponentRes.get();
        return trinketComponent.isEquipped(HexGloopItems.CASTING_RING_ITEM.get());
    }
}
