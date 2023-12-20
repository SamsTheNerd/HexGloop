package com.samsthenerd.hexgloop.mixins.booktweaks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import at.petrak.hexcasting.interop.patchouli.PatternProcessor;

@Mixin(PatternProcessor.class)
public interface MixinClientYoinkPPLangKey {
    @Accessor("translationKey")
    String getTranslationKey();
}
