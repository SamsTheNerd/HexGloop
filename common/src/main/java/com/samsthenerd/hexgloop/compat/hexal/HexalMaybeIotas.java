package com.samsthenerd.hexgloop.compat.hexal;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.block.Block;
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

    public static Either<Item, Block> getBlockOrItemFromIota(Iota iota){
        if(iota instanceof ItemTypeIota itemTypeIota){
            return itemTypeIota.getEither();
        }
        return null;
    }
}
