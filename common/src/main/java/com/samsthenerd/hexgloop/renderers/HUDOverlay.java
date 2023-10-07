package com.samsthenerd.hexgloop.renderers;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class HUDOverlay {
    protected final Identifier textureId;
    protected final int x;
    protected final int y;
    protected final int tWidth;
    protected final int tHeight;

    private static Map<Identifier, Pair<Integer, Integer>> textureSizes = new HashMap<>();
    public static final Identifier MIRROR_WIDGET_TEXTURE_ID = new Identifier(HexGloop.MOD_ID, "textures/gui/mirrorwidgets.png");

    static {
        textureSizes.put(MIRROR_WIDGET_TEXTURE_ID, new Pair<>(64, 64));
    }


    public static final HUDOverlay BOUND_MIRROR_INDICATOR = new HUDOverlay(MIRROR_WIDGET_TEXTURE_ID, 0, 0, 22, 22);
    public static final HUDOverlay HAND_MIRROR_INDICATOR = new HUDOverlay(MIRROR_WIDGET_TEXTURE_ID, 0, 24, 22, 22);
    public static final HUDOverlay SELECTED_BOUND_MIRROR_INDICATOR = new HUDOverlay(MIRROR_WIDGET_TEXTURE_ID, 24, 0, 22, 22);
    public static final HUDOverlay SELECTED_HAND_MIRROR_INDICATOR = new HUDOverlay(MIRROR_WIDGET_TEXTURE_ID, 24, 24, 22, 22);
    
    public void render(int atX, int atY, double width, double height, float tickDelta){
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buffer = tess.getBuffer();
        buffer.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        buffer.vertex(atX, atY + height, 0).texture(getMinU(), getMaxV()).next();
        buffer.vertex(atX + width, atY + height, 0).texture(getMaxU(), getMaxV()).next();
        buffer.vertex(atX + width, atY, 0).texture(getMaxU(), getMinV()).next();
        buffer.vertex(atX, atY, 0).texture(getMinU(), getMinV()).next();

        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        tess.draw();
    }

    public float getMinU(){
        return (float)x / (float)textureSizes.get(textureId).getLeft();
    }

    public float getMaxU(){
        return (float)(x + tWidth) / (float)textureSizes.get(textureId).getLeft();
    }

    public float getMinV(){
        return (float)y / (float)textureSizes.get(textureId).getRight();
    }

    public float getMaxV(){
        return (float)(y + tHeight) / (float)textureSizes.get(textureId).getRight();
    }
   

    public HUDOverlay(Identifier textureId, int x, int y, int tWidth, int tHeight){
        this.textureId = textureId;
        this.x = x;
        this.y = y;
        this.tWidth = tWidth;
        this.tHeight = tHeight;
    }
}
