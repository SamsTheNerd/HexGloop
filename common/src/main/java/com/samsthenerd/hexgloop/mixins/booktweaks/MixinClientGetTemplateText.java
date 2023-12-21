package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.text.Text;
import vazkii.patchouli.client.book.template.component.ComponentText;

@Mixin(ComponentText.class)
public interface MixinClientGetTemplateText {
    @Accessor("actualText")
    Text getActualText();
}
