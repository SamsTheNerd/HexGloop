package com.samsthenerd.hexgloop.mixins.wnboi;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemFidget;

import at.petrak.hexcasting.client.ShiftScrollListener;
import net.minecraft.item.Item;

@Mixin(ShiftScrollListener.class)
public class MixinAddScrollableListener {
    @Inject(at = @At("HEAD"), method = "IsScrollableItem(Lnet/minecraft/item/Item;)Z", cancellable = true)
    private static void MakeItScrollable(Item item, CallbackInfoReturnable<Boolean> info) {
        if(item == HexGloopItems.MULTI_FOCUS_ITEM.get() || item instanceof ItemFidget || item == HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get()){
            info.setReturnValue(true);
        }
    }
}
