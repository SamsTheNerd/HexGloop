package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.misc.clientgreatbook.GreatBook;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.common.items.ItemScroll;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

@Mixin(Item.class)
public class MixinClientClicksForGreatBook {
    
    // not in hand
    @Inject(
        method="onClicked(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/util/ClickType;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/inventory/StackReference;)Z",
        at=@At("HEAD")
    )
    public void onBookClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if(player.getWorld().isClient && stack.getItem() instanceof ItemModBook modBook){ 
            Book book = ItemModBook.getBook(stack);
            if(book == null || !book.id.toString().equals("hexcasting:thehexbook")) return;
            // should be hex book
            if(otherStack.getItem() instanceof ItemScroll scrollItem){
                try{
                    if(scrollItem.readIota(otherStack, null) instanceof PatternIota pIota){
                        GreatBook.INSTANCE.savePattern(pIota.getPattern());
                    }
                } catch(Exception e){
                    // do nothing, just to catch any issues with using null
                }
                return;
            }
            if(otherStack.getItem() == Items.SHEARS){
                GreatBook.INSTANCE.clearAll();
                return;
            }
        }
    }

    // in hand
    // @Inject(
    //     method="onStackClicked(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/util/ClickType;Lnet/minecraft/entity/player/PlayerEntity;)Z",
    //     at=@At("HEAD")
    // )
    // public void onBookStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {

    // }
}
