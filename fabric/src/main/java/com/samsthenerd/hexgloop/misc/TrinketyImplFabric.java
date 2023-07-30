package com.samsthenerd.hexgloop.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import dev.emi.trinkets.api.SlotType;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

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

    public Map<String, List<ItemStack>> getTrinkets(LivingEntity player){
        Optional<TrinketComponent> trinketComponentRes = TrinketsApi.getTrinketComponent(player);
        Map<String, List<ItemStack>> trinketMap = new HashMap<String, List<ItemStack>>();
        if(!trinketComponentRes.isPresent()) return trinketMap;
        TrinketComponent trinketComponent = trinketComponentRes.get();
        trinketComponent.forEach((slot, stack) -> {
            SlotType slotType = slot.inventory().getSlotType();
            String slotId = slotType.getGroup() + "/" + slotType.getName();
            if(standardizedSlots.containsKey(slotId)){
                slotId = standardizedSlots.get(slotId);
            }
            if(!trinketMap.containsKey(slotId)){
                trinketMap.put(slotId, new ArrayList<ItemStack>());
            }
            trinketMap.get(slotId).add(stack);
        });
        return trinketMap;
    }
}
