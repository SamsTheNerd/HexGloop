package com.samsthenerd.hexgloop.mixins.textpatterns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(targets = "at.petrak.hexcasting.api.spell.iota.PatternIota$1")
public abstract class MixinChangePatternDisplay {
    @Inject(method = "display(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
    public void display(NbtElement tag, CallbackInfoReturnable<Text> cir) {
        cir.setReturnValue(Text.literal("!").setStyle(((PatternStyle) Style.EMPTY.withBold(true).withColor(Formatting.DARK_PURPLE)).withPattern(PatternIota.deserialize(tag).getPattern())));
    }
}
