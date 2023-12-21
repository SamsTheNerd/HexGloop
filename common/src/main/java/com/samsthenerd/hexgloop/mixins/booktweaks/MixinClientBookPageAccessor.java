package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;

@Mixin(BookPage.class)
public interface MixinClientBookPageAccessor {
    @Accessor("entry")
    BookEntry getEntry();

    @Accessor("pageNum")
    int getPageNum();

    @Accessor("anchor")
    String getAnchor();
}
