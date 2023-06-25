package com.samsthenerd.hexgloop.mixins.dyeablestaffs;

import org.spongepowered.asm.mixin.Mixin;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

@Mixin(ItemStaff.class)
public class MixinInjectDyeableStaff implements DyeableItem{

    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return nbtCompound.getInt(COLOR_KEY);
        }
        return 0xff7e4f;
    }
}