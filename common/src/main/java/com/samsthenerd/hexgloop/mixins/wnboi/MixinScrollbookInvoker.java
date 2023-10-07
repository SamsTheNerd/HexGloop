package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import at.petrak.hexcasting.common.network.MsgShiftScrollSyn;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(MsgShiftScrollSyn.class)
public interface MixinScrollbookInvoker {
    @Invoker("spellbook")
    public void invokeHandleSpellbook(ServerPlayerEntity sender, Hand hand, ItemStack stack, double delta);
}
