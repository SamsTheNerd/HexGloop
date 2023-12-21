package com.samsthenerd.hexgloop.mixins.booktweaks;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.client.book.template.BookTemplate;
import vazkii.patchouli.client.book.template.TemplateComponent;

@Mixin(BookTemplate.class)
public interface MixinClientGetTemplateComponents {
    @Accessor("components")
    List<TemplateComponent> getComponents();

    @Accessor("processor")
    IComponentProcessor getProcessor();
}
