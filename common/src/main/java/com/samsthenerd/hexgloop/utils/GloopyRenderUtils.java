package com.samsthenerd.hexgloop.utils;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.mixins.textpatterns.MixinSetTessBuffer;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.client.RenderLib;
import kotlin.Pair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.ColorHelper.Argb;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec2f;

public class GloopyRenderUtils {
    public static void renderGuiItemIcon(ItemStack stack, int x, int y, int size) {
        renderGuiItemModel(stack, x, y, size, MinecraftClient.getInstance().getItemRenderer().getModel(stack, null, null, 0));
    }

    public static void renderGuiItemModel(ItemStack stack, int x, int y, int size, BakedModel model) {
        boolean bl;
        MinecraftClient.getInstance().getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.translate(x, y, 100.0f + MinecraftClient.getInstance().getItemRenderer().zOffset);
        matrixStack.translate(size/2, size/2, 0.0);
        matrixStack.scale(1.0f, -1.0f, 1.0f);
        matrixStack.scale(size, size, size);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrixStack2 = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        boolean bl2 = bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }

    public static void drawPattern(MatrixStack matrices, HexPattern pattern, int width, int height, int x, int y, int color, int dotColor, float speed, float variance){
        drawPattern(matrices, pattern, width, height, x, y, 0xFF_FFFFFF, dotColor & 0x00ffffff | 0x80000000, dotColor & 0x00ffffff | 0xc8000000, dotColor, speed, variance, false);
    }

    public static void drawPattern(MatrixStack matrices, HexPattern pattern, int width, int height, int x, int y, int outerColor, int innerColorMain, int innerColorAccent, int dotColor, float speed, float variance, boolean hasStartingDot){
        Pair<Float, List<Vec2f> > pair = RenderLib.getCenteredPattern(pattern, width, height, Math.min(Math.min(width, height), 16f));

        List<Vec2f> dots = pair.getSecond();

        List<Vec2f> zappyPointsCentered = RenderLib.makeZappy(
            dots, RenderLib.findDupIndices(pattern.positions()),
            10, variance, speed, 0f, RenderLib.DEFAULT_READABILITY_OFFSET, RenderLib.DEFAULT_LAST_SEGMENT_LEN_PROP,
            0.0);
        List<Vec2f> zappyPoints = new ArrayList<Vec2f>();
        List<Vec2f> pathfinderDots = new ArrayList<Vec2f>();

        float minY = 1000000;
        float maxY = -1000000;
        float minX = 1000000;
        float maxX = -1000000;

        // get the ranges of the points
        for(Vec2f p : dots){
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
        float scale = Math.min((width-lineWidth/2) / patWidth, (height-lineWidth/2) / patHeight);
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

        if(innerColorMain == 0xFFFFFF){
            lineScale *= 0.75;
        }

        for(Vec2f p : zappyPointsCentered){
            zappyPoints.add(new Vec2f((scale*p.x) + x, (scale*p.y) + y));
        }

        for(Vec2f p : dots){
            pathfinderDots.add(new Vec2f((scale*p.x) + x, (scale*p.y) + y));
        }

        // yoinked and adapted from pattern tooltip
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        
        // var outer = 0xff_d2c8c8;
        // var innerLight = 0xc8_aba2a2;
        // var innerDark = 0xc8_322b33;
        Matrix4f mat = matrices.peek().getPositionMatrix();

        mat.multiplyByTranslation(0f,0f,0.011f);

        // int innerColorLight = color & 0x00ffffff | 0xc8000000;
        // int innerColorDark = color & 0x00ffffff | 0x80000000; // ish

        // store what the tessellator was before
        Tessellator tessHold = Tessellator.getInstance();
        // make a new tessellator for our rendering functions to use
        Tessellator newTess = new Tessellator();
        MixinSetTessBuffer.setInstance(newTess);

        // VertexConsumer vc = vertexConsumers.getBuffer(RenderLayer.getText(new Identifier("")));

        RenderLib.drawLineSeq(mat, zappyPoints, lineWidth * lineScale, 0,
            outerColor, outerColor);
        RenderLib.drawLineSeq(mat, zappyPoints, lineWidth * 0.4f * lineScale, 0.01f,
            // style.isStrikethrough() && style.getColor() != null ? innerColorDark : innerDark, 
            // style.isStrikethrough() && style.getColor() != null ? innerColorLight : innerLight);
            innerColorMain, innerColorAccent);

        Matrix4f dotMat = mat.copy();
        dotMat.multiplyByTranslation(0f, 0f, -(+1f-0.02f)); // dot renders 1F forward for some reason, push it back a bit so it doesn't poke out on signs

        RenderLib.drawSpot(dotMat, zappyPoints.get(0), startingDotWidth*lineScale, Argb.getRed(dotColor)/255f, Argb.getGreen(dotColor)/255f, Argb.getBlue(dotColor)/255f, hasStartingDot ? 0.7f : 0f);

        dotMat.multiplyByTranslation(0, 0, 0.005f); // move the other dots just a tiny bit forwards

        for (var dot : pathfinderDots) {
            RenderLib.drawSpot(dotMat, dot, dotWidth*lineScale, 0.82f, 0.8f, 0.8f, 0.5f);
        }

        // return tessellator instance back to what it was before
        MixinSetTessBuffer.setInstance(tessHold);
    }
}
