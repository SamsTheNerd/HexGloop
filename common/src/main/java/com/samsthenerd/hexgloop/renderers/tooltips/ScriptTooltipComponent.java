package com.samsthenerd.hexgloop.renderers.tooltips;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.items.tooltips.ScriptTooltipData;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.client.RenderLib;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;

public class ScriptTooltipComponent implements TooltipComponent {

    private static final float RENDER_SIZE = 128f;

    private final Identifier background;
    private final List<HexPattern> patterns;
    private final List<RenderablePattern> renderablePatterns;
    private final int cols;
    private final int rows;
    private final float size;

    public ScriptTooltipComponent(ScriptTooltipData tt) {
        this.patterns = tt.patterns;
        this.background = tt.background;

        int patternCount = patterns.size();
        cols = (int) Math.ceil(Math.sqrt(patternCount));
        rows = (int) Math.ceil(patternCount / (float) cols);
        size = (RENDER_SIZE*0.8f) / Math.max(cols, rows);
        renderablePatterns = new ArrayList<>();
        for(HexPattern pat : patterns){
            renderablePatterns.add(new RenderablePattern(pat, size));
        }
    }

    @Override
    public void drawItems(TextRenderer font, int mouseX, int mouseY, MatrixStack ps, ItemRenderer pItemRenderer,
                            int pBlitOffset) {
        var width = this.getWidth(font);
        var height = this.getHeight();

        // far as i can tell "mouseX" and "mouseY" are actually the positions of the corner of the tooltip
        ps.push();
        ps.translate(mouseX, mouseY, 500);
        RenderSystem.enableBlend();
        renderBG(ps, this.background, pBlitOffset);

        // renderText happens *before* renderImage for some asinine reason
//                RenderSystem.disableBlend();
        float top = (height - (size * rows))/2f;
        float left = (width - (size * cols))/2f;
        ps.translate(left, top, 0);
        for(int i = 0; i < patterns.size(); i++){
            int row = i / cols;
            int col = i % cols;
            ps.push();
            renderablePatterns.get(i).render(ps, col*size, row*size, pBlitOffset);
            ps.pop();
        }
        ps.pop();
    }

    private static void renderBG(MatrixStack ps, Identifier background, int blitOffset) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, background);
        // x y blitoffset sw sh w h ... ?
        // parchment doesn't have this mapped
        DrawableHelper.drawTexture(ps, 0, 0, blitOffset, 0f, 0f, (int) RENDER_SIZE, (int) RENDER_SIZE, (int) RENDER_SIZE,
            (int) RENDER_SIZE);
    }

    @Override
    public int getWidth(TextRenderer pFont) {
        return (int) RENDER_SIZE;
    }

    @Override
    public int getHeight() {
        return (int) RENDER_SIZE;
    }

    public static class RenderablePattern{
        public final HexPattern pattern;
        public final List<Vec2f> zappyPoints;
        public final List<Vec2f> pathfinderDots;
        public final float scale;
        public final float size;

        // size is the size of this pattern
        public RenderablePattern(HexPattern pattern, float size){
            this.pattern = pattern;
            this.size = size;
            var pair = RenderLib.getCenteredPattern(pattern, size, size, 16f);
            this.scale = pair.getFirst();
            var dots = pair.getSecond();
            this.zappyPoints = RenderLib.makeZappy(
                dots, RenderLib.findDupIndices(pattern.positions()),
                10, 0.8f, 0f, 0f, RenderLib.DEFAULT_READABILITY_OFFSET, RenderLib.DEFAULT_LAST_SEGMENT_LEN_PROP,
                0.0);
            this.pathfinderDots = dots.stream().distinct().collect(Collectors.toList());
        }

        // x and y should have offsets baked in
        public void render(MatrixStack ps, float x, float y, int blitOffset){
            ps.push();
            ps.translate(x, y, 100);

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableCull();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
                GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            ps.translate(size / 2f, size / 2f, 1);

            var mat = ps.peek().getPositionMatrix();
            var outer = 0xff_d2c8c8;
            var innerLight = 0xc8_aba2a2;
            var innerDark = 0xc8_322b33;
            float widthScale = (float)Math.sqrt(size / (RENDER_SIZE*0.8));
            RenderLib.drawLineSeq(mat, this.zappyPoints, 6f * widthScale, 0,
                outer, outer);
            RenderLib.drawLineSeq(mat, this.zappyPoints, 6f * 0.4f * widthScale, 0,
                innerDark, innerLight);
            RenderLib.drawSpot(mat, this.zappyPoints.get(0), 2.5f*widthScale, 1f, 0.1f, 0.15f, 0.6f);

            for (var dot : this.pathfinderDots) {
                RenderLib.drawSpot(mat, dot, 1.5f*widthScale, 0.82f, 0.8f, 0.8f, 0.5f);
            }

            ps.pop();
        }
    }
}
