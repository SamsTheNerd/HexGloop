package com.samsthenerd.hexgloop.mixins.textpatterns;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.screens.PatternStyle;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.client.RenderLib;
import kotlin.Pair;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Style;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
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

    @Shadow
    @Final
    VertexConsumerProvider vertexConsumers;

    private static final float RENDER_SIZE = 128f;

    @Inject(method = "accept(ILnet/minecraft/text/Style;I)Z", at = @At("HEAD"), cancellable = true)
	private void PatStyDrawerAccept(int index, Style style, int codepoint, CallbackInfoReturnable<Boolean> cir) {
        PatternStyle pStyle = (PatternStyle) style;

        
        if(pStyle.getPattern() == null){
            return;
        } else {
            HexPattern pattern = pStyle.getPattern();
            Pair<Float, List<Vec2f> > pair = RenderLib.getCenteredPattern(pattern, RENDER_SIZE, RENDER_SIZE, 16f);
            Float patScale = pair.getFirst();


            List<Vec2f> dots = pair.getSecond();

            float speed = 0;
            float variance = 0;

            // have it in stages so italics is kinda wobbly, obfuscated is really wobbly, and both is really really wobbly

            if(style.isItalic() && !style.isObfuscated()){
                speed = 0.05f;
                variance = 0.2f;
            } else if(style.isObfuscated() && !style.isItalic()){
                speed = 0.1f;
                variance = 0.8f;
            } else if(style.isObfuscated() && style.isItalic()){
                speed = 0.15f;
                variance = 3f;
            }

            List<Vec2f> zappyPointsCentered = RenderLib.makeZappy(
                dots, RenderLib.findDupIndices(pattern.positions()),
                10, variance, speed, 0f, RenderLib.DEFAULT_READABILITY_OFFSET, RenderLib.DEFAULT_LAST_SEGMENT_LEN_PROP,
                0.0);
            List<Vec2f> zappyPointsCenteredStill = pStyle.getZappyPoints();
            List<Vec2f> pathfinderDotsCentered = pStyle.getPathfinderDots();
            List<Vec2f> zappyPoints = new ArrayList<Vec2f>();
            List<Vec2f> pathfinderDots = new ArrayList<Vec2f>();

            float minY = 1000000;
            float maxY = -1000000;
            float minX = 1000000;
            float maxX = -1000000;

            // get the ranges of the points
            for(Vec2f p : zappyPointsCenteredStill){
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
            float scale = (9f-(lineWidth*0.75f)) / Math.max(patHeight, 48); // don't let it get absurdly long
            float lineScale = 1f;

            // done weird like this so it's in steps, probably a way to do it more mathematically but oh well
            if(scale / 0.5 < 0.5){
                lineScale /= 1.5;
            }
            if(scale / 0.5 < 0.25){
                lineScale /= 1.5;
            }
            if(scale / 0.5 < 0.125){
                lineScale /= 1.5;
            }
            if(scale / 0.5 < 0.0625){
                lineScale /= 1.5;
            }

            if((style.isStrikethrough() && style.getColor().equals(TextColor.fromFormatting(Formatting.WHITE))) || style.getColor() == null){
                lineScale *= 0.75;
            }


            for(Vec2f p : zappyPointsCentered){
                zappyPoints.add(new Vec2f((scale*p.x) + x + (scale*patWidth/2), (scale*p.y) + y + (scale*patHeight/2)));
            }

            for(Vec2f p : pathfinderDotsCentered){
                pathfinderDots.add(new Vec2f((scale*p.x) + x + (scale*patWidth/2), (scale*p.y) + y + (scale*patHeight/2)));
            }

            // yoinked and adapted from pattern tooltip
            RenderSystem.enableBlend();
            RenderSystem.enableDepthTest();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableCull();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

            // no clue how this will go !
            
            var outer = 0xff_d2c8c8;
            var innerLight = 0xc8_aba2a2;
            var innerDark = 0xc8_322b33;
            Matrix4f mat = this.matrix.copy();

            mat.multiplyByTranslation(0f,0f,0.011f);

            int color = 0xffffffff;
            if(style.getColor() != null) color = style.getColor().getRgb();
            int innerColorLight = color & 0x00ffffff | 0xc8000000;
            int innerColorDark = color & 0x00ffffff | 0x80000000; // ish

            // store what the tessellator was before
            Tessellator tessHold = Tessellator.getInstance();
            // make a new tessellator for our rendering functions to use
            Tessellator newTess = new Tessellator();
            MixinSetTessBuffer.setInstance(newTess);

            // VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("")));

            RenderLib.drawLineSeq(mat, zappyPoints, lineWidth * lineScale, 0,
                0xffffffff, 0xffffffff);
            RenderLib.drawLineSeq(mat, zappyPoints, lineWidth * 0.4f * lineScale, 0.01f,
                // style.isStrikethrough() && style.getColor() != null ? innerColorDark : innerDark, 
                // style.isStrikethrough() && style.getColor() != null ? innerColorLight : innerLight);
                innerColorDark, innerColorLight);

            Matrix4f dotMat = mat.copy();
            dotMat.multiplyByTranslation(0f, 0f, -(+1f-0.02f)); // dot renders 1F forward for some reason, push it back a bit so it doesn't poke out on signs

            RenderLib.drawSpot(dotMat, zappyPoints.get(0), startingDotWidth*lineScale, Argb.getRed(color)/255f, Argb.getGreen(color)/255f, Argb.getBlue(color)/255f, style.isBold() ? 0.7f : 0f);

            dotMat.multiplyByTranslation(0, 0, 0.005f); // move the other dots just a tiny bit forwards

            for (var dot : pathfinderDots) {
                RenderLib.drawSpot(dotMat, dot, dotWidth*lineScale, 0.82f, 0.8f, 0.8f, 0.5f);
            }

            // return tessellator instance back to what it was before
            MixinSetTessBuffer.setInstance(tessHold);

            this.x += patWidth * scale + 1f;

            // not my business what happens after this?
            cir.setReturnValue(true);
        }
    }
}
