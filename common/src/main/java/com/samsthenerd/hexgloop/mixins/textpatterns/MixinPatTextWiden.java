package com.samsthenerd.hexgloop.mixins.textpatterns;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.screens.PatternStyle;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import net.minecraft.util.math.Vec2f;

@Mixin(TextRenderer.class)
public class MixinPatTextWiden {
    @Inject(method = "method_27516(ILnet/minecraft/text/Style;)F", at = @At("HEAD"), cancellable = true)
    private void TextHandlerOverrideForPattern(int codepoint, Style style, CallbackInfoReturnable<Float> cir){
        PatternStyle pStyle = (PatternStyle)style;
        if(pStyle.getPattern() != null){
            // should prob abstract some of this into pattern style
            List<Vec2f> zappyPointsCentered = pStyle.getZappyPoints();

            float minY = 1000000;
            float maxY = -1000000;
            float minX = 1000000;
            float maxX = -1000000;

            // get the ranges of the points
            for(Vec2f p : zappyPointsCentered){
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
            }

            // putting these out here since we might want to account for line thickness?
            int patWidth = (int)(maxX - minX);
            int patHeight = (int)(maxY - minY);

            // ideally fit into normal height ? maybe *slightly* taller?
            // I think it's 10f for both?
            float scale = (9f-1.8f) / Math.max(patHeight, 48);
            cir.setReturnValue(scale*patWidth+2f);
        }
    }

}
