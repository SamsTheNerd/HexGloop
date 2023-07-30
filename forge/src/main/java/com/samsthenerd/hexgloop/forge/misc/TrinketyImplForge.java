package com.samsthenerd.hexgloop.forge.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.misc.ITrinkety;

import at.petrak.hexcasting.interop.HexInterop;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

// it says trinkety but it's curios for forge
public class TrinketyImplForge implements ITrinkety{
    public TrinketyImplForge(){

    }

    @Override
    public boolean isCastingRingEquipped(LivingEntity player){
        return !CuriosApi.getCuriosHelper().findCurios(player, HexGloopItems.CASTING_RING_ITEM.get()).isEmpty();
    }

    public Map<String, List<ItemStack>> getTrinkets(LivingEntity player){
        List<SlotResult> curiosList = CuriosApi.getCuriosHelper().findCurios(player, (stack) -> true);
        Map<String, List<ItemStack>> trinketMap = new HashMap<String, List<ItemStack>>();
        for(SlotResult sr : curiosList){
            String slotId = sr.slotContext().identifier();
            if(standardizedSlots.containsKey(slotId)){
                slotId = standardizedSlots.get(slotId);
            }
            if(!trinketMap.containsKey(slotId)){
                trinketMap.put(slotId, new ArrayList<ItemStack>());
            }
            trinketMap.get(slotId).add(sr.stack());
        }
        return trinketMap;
    }

    public static void onInterModEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo(HexInterop.Forge.CURIOS_API_ID, SlotTypeMessage.REGISTER_TYPE, 
            () -> SlotTypePreset.RING.getMessageBuilder().build() );
    }
}
