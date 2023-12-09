package com.samsthenerd.hexgloop.compat.hexal;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.item.Item;
import ram.talia.hexal.api.spell.iota.ItemTypeIota;

public class HexalMaybeIotas {
    public static Iota getTypeIota(Item item){
        return new ItemTypeIota(item);
    }

    @Nullable
    public static Item getItemFromIota(Iota iota){
        if(iota instanceof ItemTypeIota){
            return ((ItemTypeIota)iota).getItem();
        }
        return null;
    }
}
