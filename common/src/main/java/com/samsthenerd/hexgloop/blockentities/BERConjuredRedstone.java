package com.samsthenerd.hexgloop.blockentities;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BERConjuredRedstone implements BlockEntityRenderer<BlockEntityConjuredRedstone> {

    public static Map<Direction, Vec3i> startingVectors = new HashMap<Direction, Vec3i>();

    static {
        startingVectors.put(Direction.DOWN, new Vec3i(1, -1, 1));
        startingVectors.put(Direction.UP, new Vec3i(-1, 1, -1));
        startingVectors.put(Direction.NORTH, new Vec3i(-1, -1, -1));
        startingVectors.put(Direction.SOUTH, new Vec3i(1, -1, 1));
        startingVectors.put(Direction.WEST, new Vec3i(-1, -1, 1));
        startingVectors.put(Direction.EAST, new Vec3i(1, -1, -1));
    }

    public BERConjuredRedstone(BlockEntityRendererFactory.Context ctx){

    }

    public void render(BlockEntityConjuredRedstone be, float tickDelta, MatrixStack stack, VertexConsumerProvider provider, int light, int overlay){
        // VertexConsumer buffer = provider.getBuffer(RenderLayer.getTranslucent());
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
        int power = be.getPower();
        FrozenColorizer colorizer = be.getColorizer();
        Identifier textureId = new Identifier(HexGloop.MOD_ID, "block/conjured_redstone");
        Sprite sprite = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, textureId).getSprite();
        float time = be.getWorld().getTime();
        for(Direction dir : Direction.values()){
            Vec3i normal = dir.getVector();
            Vec3i nextVec = startingVectors.get(dir);
            Vec3d baseWorldVec = new Vec3d(be.getPos().getX(), be.getPos().getY(), be.getPos().getZ());
            for(int i = 0; i < 4; i++){
                Vec3d worldVec = baseWorldVec.add(new Vec3d((nextVec.getX()+1)/2, (nextVec.getY()+1)/2, (nextVec.getZ()+1)/2));
                int color = colorizer.getColor(time+tickDelta, worldVec);
                buffer.vertex(stack.peek().getPositionMatrix(), (nextVec.getX()+1)/2, (nextVec.getY()+1)/2, (nextVec.getZ()+1)/2)
                    .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, ((power+16) * 8)-1)
                    // .texture((((i >> 1) & 1) ^ (i & 1)) == 0 ? sprite.getMinU() : sprite.getMaxU(), ((i >> 1) & 1) == 0 ? sprite.getMinV() : sprite.getMaxV())
                    .texture( ((i >> 1) & 1) == 0 ? sprite.getMinU() : sprite.getMaxU(), (((i >> 1) & 1) ^ (i & 1)) == 0 ? sprite.getMinV() : sprite.getMaxV())
                    .light(light).normal(stack.peek().getNormalMatrix(), normal.getX(), normal.getY(), normal.getZ())
                    .next();
                nextVec = normal.add(normal.crossProduct(nextVec));
            }
        }
        // RenderSystem.setShaderTexture(0, new Identifier(HexGloop.MOD_ID, "textures/block/conjured_redstone.png"));
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        BlockPos camPos = MinecraftClient.getInstance().getCameraEntity().getBlockPos();
        RenderLayer.getTranslucent().draw(buffer, camPos.getX(), camPos.getY(), camPos.getZ());
    }



}
