package com.samsthenerd.hexgloop.mixins.canvas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

@Mixin(Vec3Iota.class)
public class MixinPrettyVectors {
    @ModifyReturnValue(
        method="display(DDD)Lnet/minecraft/text/Text;",
        at=@At("RETURN")
    )
    private static Text makeColorful(Text original, double x, double y, double z){
        // check that it's 
        if(x >= 0 && x <= 255 && y >= 0 && y <= 255 && z >= 0 && z <= 255){
            int color = ((int) x) << 16 | ((int) y) << 8 | ((int) z);
            Style style = original.getStyle().withColor(color);
            MutableText colorMarker = Text.literal("█").setStyle(style);
            return original.copy().append(colorMarker);
        }
        return original;
    }
}
