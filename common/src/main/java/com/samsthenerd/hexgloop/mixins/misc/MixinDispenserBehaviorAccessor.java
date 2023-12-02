package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.item.ItemStack;

@Mixin(DispenserBlock.class)
public interface MixinDispenserBehaviorAccessor {
    @Invoker("getBehaviorForItem")
    DispenserBehavior callGetBehaviorForItem(ItemStack stack);
}
