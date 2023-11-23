package com.samsthenerd.hexgloop.items;

import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;

public class ItemCopingSaw extends Item{
    public ItemCopingSaw(Settings settings){
        super(settings);
    }

    public static Modification overcastModifer(CastingContext ctx, Boolean original){
        if(ctx.getCaster() != null){
            PlayerInventory pInv = ctx.getCaster().getInventory();
            for(int i = 0; i < PlayerInventory.getHotbarSize(); i++){
                if(pInv.getStack(i).getItem() == HexGloopItems.COPING_SAW_ITEM.get()){
                    return Modification.DISABLE;
                }
            }
        }
        return Modification.NONE;
    }
}
