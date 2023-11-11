package com.samsthenerd.hexgloop.mixins.textpatterns;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(targets = "at.petrak.hexcasting.api.spell.iota.PatternIota$1")
public abstract class MixinChangePatternDisplay {
    @Inject(method = "display(Lnet/minecraft/nbt/NbtElement;)Lnet/minecraft/text/Text;", at = @At("HEAD"), cancellable = true)
    public void makePatternIotaVisual(NbtElement tag, CallbackInfoReturnable<Text> cir) {
        HexPattern pattern = PatternIota.deserialize(tag).getPattern();
        Style patternStyle = ((PatternStyle) Style.EMPTY.withBold(true).withStrikethrough(true).withColor(Formatting.WHITE)).withPattern(pattern);
        Text originalText = PatternIota.display(pattern); // get the original text so we can keep the string the same
        String originalString = originalText.getString();
        MutableText patternTextStyled = Text.literal(originalString.substring(0, 1)).setStyle(patternStyle);
        String hiddenString = originalString.substring(1);
        patternTextStyled.append(Text.literal(hiddenString).setStyle(Style.EMPTY.withHidden(true)));

        cir.setReturnValue(patternTextStyled);
    }
}
