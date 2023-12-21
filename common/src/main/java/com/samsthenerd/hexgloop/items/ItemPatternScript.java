package com.samsthenerd.hexgloop.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.items.tooltips.ScriptTooltipData;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemPatternScript extends Item implements IotaHolderItem{

    public static final String TAG_DATA = "data";
    public static final int MAX_PATTERN_COUNT = 16;

    public ItemPatternScript(Settings settings){
        super(settings);
    }

    // erase or check for if it only has patterns
    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return iota == null || patternsOnly(iota) != null;
    }

    public Iota patternsOnly(Iota iota){
        if(iota instanceof PatternIota){
            return iota;
        }
        if(iota instanceof ListIota lIota){
            // limit number of patterns
            if(lIota.getList().size() > MAX_PATTERN_COUNT){
                return null;
            }
            for(Iota inIota : lIota.getList()){
                // null if there's a non-pattern iota in there
                if(!(inIota instanceof PatternIota)){
                    return null;
                }
            }
            return iota;
        }
        return null;
    }

    @Override
    public NbtCompound readIotaTag(ItemStack stack){
        return NBTHelper.getCompound(stack, TAG_DATA);
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if (iota == null) {
            stack.removeSubNbt(TAG_DATA);
        } else {
            NBTHelper.put(stack, TAG_DATA, HexIotaTypes.serialize(patternsOnly(iota)));
        }
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        try{
            Iota storedIota = readIota(stack, null);
            if(storedIota instanceof ListIota lIota){
                List<HexPattern> patterns = new ArrayList<>();
                for(Iota iota : lIota.getList()){
                    if(iota instanceof PatternIota pIota){
                        patterns.add(pIota.getPattern());
                    }
                }
                return Optional.of(new ScriptTooltipData(patterns, PatternTooltipComponent.ANCIENT_BG));
            }
        } catch (Exception e){

        }
        return Optional.empty();
    }
}
