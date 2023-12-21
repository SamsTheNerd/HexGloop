package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vazkii.patchouli.client.book.page.PageTemplate;
import vazkii.patchouli.client.book.template.BookTemplate;

@Mixin(PageTemplate.class)
public interface MixinClientGetPageTemplate {
    @Accessor("template")
    BookTemplate getTemplate();
}
