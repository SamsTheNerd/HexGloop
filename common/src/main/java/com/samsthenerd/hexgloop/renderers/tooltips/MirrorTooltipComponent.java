package com.samsthenerd.hexgloop.renderers.tooltips;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.items.tooltips.MirrorTooltipData;
import com.samsthenerd.hexgloop.renderers.HUDOverlay;
import com.samsthenerd.hexgloop.utils.GloopyRenderUtils;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

// yoinked and modified from hex

public class MirrorTooltipComponent implements TooltipComponent {

    private static final float RENDER_SIZE = 64f;

    private final ItemStack storedItem;

    public MirrorTooltipComponent(MirrorTooltipData tt) {
        this.storedItem = tt.storedItem();
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
        renderBG(ps, HUDOverlay.SELECTED_HAND_MIRROR_INDICATOR, pBlitOffset);

        // renderText happens *before* renderImage for some asinine reason
//                RenderSystem.disableBlend();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA,
            GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);

        int itemSize = 32;

        GloopyRenderUtils.renderGuiItemIcon(storedItem, mouseX + width /2 - itemSize/2, mouseY + height / 2 - itemSize/2, itemSize);
        // pItemRenderer.renderGuiItemIcon(storedItem, mouseX + width /2, mouseY + height / 2);

        ps.pop();
    }

    private static void renderBG(MatrixStack ps, Identifier background, int blitOffset, float u, float v, int textWidth, int textHeight, 
    int sWidth, int sHeight) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, background);
        ps.push();
        ps.scale(RENDER_SIZE / (float)sWidth, RENDER_SIZE / (float)sHeight, 1f);
        DrawableHelper.drawTexture(ps, 0, 0, blitOffset, u, v, sWidth, sHeight, textWidth,
            textHeight);
        ps.pop();
    }

    private static void renderBG(MatrixStack ps, Identifier background, int blitOffset){
        renderBG(ps, background, blitOffset, 0, 0, (int)RENDER_SIZE, (int) RENDER_SIZE, (int) RENDER_SIZE, (int) RENDER_SIZE);
    }

    // idk just use this for now 
    private static void renderBG(MatrixStack ps, HUDOverlay overlay, int blitOffset){
        Pair<Integer, Integer> size = overlay.getTextureSize();
        renderBG(ps, overlay.getTextureId(), blitOffset, overlay.getMinU(), overlay.getMinV(), size.getLeft(), size.getRight(), 
            overlay.getTWidth(), overlay.getTHeight());
    }

    @Override
    public int getWidth(TextRenderer pFont) {
        return (int) RENDER_SIZE;
    }

    @Override
    public int getHeight() {
        return (int) RENDER_SIZE;
    }
}

