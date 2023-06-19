package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import at.petrak.hexcasting.client.ShiftScrollListener;
import net.minecraft.item.Item;

@Mixin(ShiftScrollListener.class)
public interface MixinIsScrollableInvoker {
    @Invoker("IsScrollableItem")
    public static boolean InvokeIsScrollableItem(Item item){
        throw new AssertionError();
    }
}
