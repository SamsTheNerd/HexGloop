package com.samsthenerd.hexgloop.forge.misc;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.misc.ITrinkety;

import at.petrak.hexcasting.interop.HexInterop;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import top.theillusivec4.curios.api.CuriosApi;
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

    public static void onInterModEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo(HexInterop.Forge.CURIOS_API_ID, SlotTypeMessage.REGISTER_TYPE, 
            () -> SlotTypePreset.RING.getMessageBuilder().build() );
    }
}
