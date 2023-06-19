package com.samsthenerd.hexgloop.mixins.dyeablestaffs;

import org.spongepowered.asm.mixin.Mixin;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.item.DyeableItem;

@Mixin(ItemStaff.class)
public class MixinInjectDyeableStaff implements DyeableItem{
    
}