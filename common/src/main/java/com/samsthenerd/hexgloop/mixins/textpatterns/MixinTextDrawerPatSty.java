package com.samsthenerd.hexgloop.mixins.textpatterns;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.client.RenderLib;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.text.Style;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;

// inspired by https://github.com/Snownee/TextAnimator/blob/1.19.2-fabric/src/main/java/snownee/textanimator/mixin/client/StringRenderOutputMixin.java
@Mixin( targets = "net.minecraft.client.font.TextRenderer$Drawer")
public class MixinTextDrawerPatSty {
    @Shadow
	float x;
	@Shadow
	float y;
    @Shadow
    private Matrix4f matrix;

    @Inject(method = "accept(ILnet/minecraft/text/Style;I)Z", at = @At("HEAD"), cancellable = true)
	private void PatStyDrawerAccept(int index, Style style, int codepoint, CallbackInfoReturnable<Boolean> cir) {
        PatternStyle pStyle = (PatternStyle) style;
        
        if(pStyle.getPattern() == null){
            return;
        } else {
            List<Vec2f> zappyPointsCentered = pStyle.getZappyPoints();
            List<Vec2f> pathfinderDotsCentered = pStyle.getPathfinderDots();
            List<Vec2f> zappyPoints = new ArrayList<Vec2f>();
            List<Vec2f> pathfinderDots = new ArrayList<Vec2f>();

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

            float lineWidth = 1.8f;
            float innerWidth = 1f; // not *really* sure how this changes it
            float dotWidth = 0.6f;
            float startingDotWidth = 1.25f; // big red one

            // putting these out here since we might want to account for line thickness?
            int patWidth = (int)(maxX - minX);
            int patHeight = (int)(maxY - minY);

            // ideally fit into normal height ? maybe *slightly* taller?
            // I think it's 10f for both?
            float scale = (9f-(lineWidth*0.75f)) / patHeight;


            for(Vec2f p : zappyPointsCentered){
                zappyPoints.add(new Vec2f((scale*p.x) + x + (scale*patWidth/2), (scale*p.y) + y + (scale*patHeight/2)));
            }


            for(Vec2f p : pathfinderDotsCentered){
                pathfinderDots.add(new Vec2f((scale*p.x) + x + (scale*patWidth/2), (scale*p.y) + y + (scale*patHeight/2)));
            }

            // yoinked and adapted from pattern tooltip
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableCull();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

            // no clue how this will go !
            
            var outer = 0xff_d2c8c8;
            var innerLight = 0xc8_aba2a2;
            var innerDark = 0xc8_322b33;
            Matrix4f mat = this.matrix.copy();

            int color = innerDark; 
            if(style.getColor() != null) color = style.getColor().getRgb();
            int innerColorLight = color & 0x00ffffff | 0xc8000000;
            int innerColorDark = color & 0x00ffffff | 0x80000000; // ish

            // store what the tessellator was before
            Tessellator tessHold = Tessellator.getInstance();
            // make a new tessellator for our rendering functions to use
            Tessellator newTess = new Tessellator();
            MixinSetTessBuffer.setInstance(newTess);

            RenderLib.drawLineSeq(mat, zappyPoints, lineWidth, 0,
                outer, outer);
            RenderLib.drawLineSeq(mat, zappyPoints, innerWidth, 0,
                style.isStrikethrough() && style.getColor() != null ? innerColorDark : innerDark, 
                style.isStrikethrough() && style.getColor() != null ? innerColorLight : innerLight);
            RenderLib.drawSpot(mat, zappyPoints.get(0), startingDotWidth, Argb.getRed(color), Argb.getGreen(color), Argb.getBlue(color), style.isBold() ? 0.7f : 0f);

            for (var dot : pathfinderDots) {
                RenderLib.drawSpot(mat, dot, dotWidth, 0.82f, 0.8f, 0.8f, 0.5f);
            }

            // return tessellator instance back to what it was before
            MixinSetTessBuffer.setInstance(tessHold);


            this.x += patWidth * scale + 1f;

            // not my business what happens after this?
            cir.setReturnValue(true);
        }
    }
}
