package com.samsthenerd.hexgloop.mixins.textpatterns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.font.TextVisitFactory;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;

@Mixin(TextVisitFactory.class)
public interface MixinVisitRegularChars {
    @Invoker("visitRegularCharacter")
    public static boolean visitRegularCharacterPublic(Style style, CharacterVisitor visitor, int index, char c) {
        throw new AssertionError();
    }
}
