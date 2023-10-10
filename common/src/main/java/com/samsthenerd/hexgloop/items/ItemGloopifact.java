package com.samsthenerd.hexgloop.items;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public class ItemGloopifact extends ItemPackagedHex implements IotaHolderItem {
    public ItemGloopifact(Settings settings){
        super(settings);
    }

    public boolean breakAfterDepletion(){
        return false;
    }

    // might make this toggleable
    public boolean canDrawMediaFromInventory(ItemStack stack){
        return true;
    }

    @Override
    public void clearHex(ItemStack stack) {
        if(readIotaTag(stack) != null){ // hopefully protect against clearing the hex before clearing the iota
            super.clearHex(stack);
        }
    }

    @Nullable
    public NbtCompound readIotaTag(ItemStack stack){
        if(stack.getNbt() != null && stack.getNbt().contains(ItemFocus.TAG_DATA))
            return NBTHelper.getCompound(stack, ItemFocus.TAG_DATA);
        return null;
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return iota == null || hasHex(stack);
    }

    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            // erase ! although maybe need to handle it so it erases iota before packaged hex
            stack.removeSubNbt(ItemFocus.TAG_DATA);
        } else {
            NBTHelper.put(stack, ItemFocus.TAG_DATA, HexIotaTypes.serialize(iota));
        }
    }

    public int cooldown(){
        return 0;
    }
}
