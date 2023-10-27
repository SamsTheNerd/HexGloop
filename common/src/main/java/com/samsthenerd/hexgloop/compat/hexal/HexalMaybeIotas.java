package com.samsthenerd.hexgloop.compat.hexal;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.item.Item;
import ram.talia.hexal.api.spell.iota.ItemTypeIota;

public class HexalMaybeIotas {
    public static Iota getTypeIota(Item item){
        return new ItemTypeIota(item);
    }
}
