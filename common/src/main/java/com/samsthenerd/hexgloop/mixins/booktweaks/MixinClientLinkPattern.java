package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Pair;
import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.misc.clientgreatbook.PatternPageLookup;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.common.items.ItemScroll;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.client.book.BookContents;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;

@Mixin(BookContents.class)
public class MixinClientLinkPattern {

    @Shadow
    @Final
    private Book book;

    @Inject(
        method="getEntryForStack(Lnet/minecraft/item/ItemStack;)Lcom/mojang/datafixers/util/Pair;",
        at=@At("HEAD"),
        cancellable=true
    )
    public void getEntryForPattern(ItemStack stack, CallbackInfoReturnable<Pair<BookEntry, Integer>> cir){
        if(book != null && book.id.toString().equals("hexcasting:thehexbook") && stack.getItem() instanceof ItemScroll scrollItem){
            try{
                Iota storedIota = scrollItem.readIota(stack, null);
                if(storedIota instanceof PatternIota patternIota){
                    BookPage page = PatternPageLookup.findPage(patternIota.getPattern());
                    if(page instanceof MixinClientBookPageAccessor pageAccessor){
                        BookEntry entry = pageAccessor.getEntry();
                        int pageIndex = pageAccessor.getPageNum() / 2;
                        cir.setReturnValue(new Pair<>(entry, pageIndex));
                    }
                }
            } catch (Exception e){
                HexGloop.logPrint("exception: " + e.toString());
            }
            
        }
    }
}
