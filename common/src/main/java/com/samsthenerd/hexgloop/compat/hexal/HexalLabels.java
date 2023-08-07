package com.samsthenerd.hexgloop.compat.hexal;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.EntityLabel;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.ItemLabel;

import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import ram.talia.hexal.api.spell.iota.EntityTypeIota;
import ram.talia.hexal.api.spell.iota.ItemTypeIota;
import ram.talia.hexal.api.spell.iota.MoteIota;

public class HexalLabels {
    public static void register(){
        LabelMaker.registerIotaLabelFunction(ItemTypeIota.TYPE, HexalLabels::itemTypeToLabel);
        LabelMaker.registerIotaLabelFunction(MoteIota.TYPE, HexalLabels::moteToLabel);
        // this one doesn't work since world is null
        // LabelMaker.registerIotaLabelFunction(EntityTypeIota.TYPE, HexalLabels::entityTypeToLabel);
    }

    @Nullable
    public static Label itemTypeToLabel(Iota iota){
        if(!(iota instanceof ItemTypeIota)) return null;
        ItemTypeIota typeIota = (ItemTypeIota) iota;
        Either<Item, Block> itemOrBlock = typeIota.getEither();
        if(itemOrBlock.left().isPresent()){
            return new ItemLabel(itemOrBlock.left().get().getDefaultStack());
        } else {
            return new ItemLabel(itemOrBlock.right().get().asItem().getDefaultStack());
        }
    }

    @Nullable
    public static Label moteToLabel(Iota iota){
        if(!(iota instanceof MoteIota)) return null;
        MoteIota moteIota = (MoteIota) iota;
        ItemStack stack = moteIota.getRecord().toStack((int)Math.min(moteIota.getCount(), 64));
        return new ItemLabel(stack);
    }

    @Nullable
    public static Label entityTypeToLabel(Iota iota){
        if(!(iota instanceof EntityTypeIota)) return null;
        EntityTypeIota typeIota = (EntityTypeIota) iota;
        EntityType<?> entType = typeIota.getEntityType();
        return new EntityLabel(entType.create(null));
    }
}
