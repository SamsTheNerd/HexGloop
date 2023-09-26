package com.samsthenerd.hexgloop.blockentities;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.BlockSlateChest;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

// code just yoinked from ChestBlockEntityRenderer
public class BERHexChest<T extends BlockEntity & LidOpenable> implements BlockEntityRenderer<T> {
   private static final String BASE = "bottom";
   private static final String LID = "lid";
   private static final String LATCH = "lock";
   private final ModelPart singleChestLid;
   private final ModelPart singleChestBase;
   private final ModelPart singleChestLatch;
   private final ModelPart doubleChestLeftLid;
   private final ModelPart doubleChestLeftBase;
   private final ModelPart doubleChestLeftLatch;
   private final ModelPart doubleChestRightLid;
   private final ModelPart doubleChestRightBase;
   private final ModelPart doubleChestRightLatch;

   
   public BERHexChest(BlockEntityRendererFactory.Context ctx) {

      ModelPart modelPart = ctx.getLayerModelPart(EntityModelLayers.CHEST);
      this.singleChestBase = modelPart.getChild("bottom");
      this.singleChestLid = modelPart.getChild("lid");
      this.singleChestLatch = modelPart.getChild("lock");
      // i think most of our chests will just be single but uhh, doesn't hurt to keep this in ?
      ModelPart modelPart2 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_LEFT);
      this.doubleChestLeftBase = modelPart2.getChild("bottom");
      this.doubleChestLeftLid = modelPart2.getChild("lid");
      this.doubleChestLeftLatch = modelPart2.getChild("lock");
      ModelPart modelPart3 = ctx.getLayerModelPart(EntityModelLayers.DOUBLE_CHEST_RIGHT);
      this.doubleChestRightBase = modelPart3.getChild("bottom");
      this.doubleChestRightLid = modelPart3.getChild("lid");
      this.doubleChestRightLatch = modelPart3.getChild("lock");
   }

   public static TexturedModelData getSingleTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), ModelTransform.NONE);
      modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
      modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(7.0F, -1.0F, 15.0F, 2.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
      return TexturedModelData.of(modelData, 64, 64);
   }

   public static TexturedModelData getRightDoubleTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
      modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
      modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(15.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
      return TexturedModelData.of(modelData, 64, 64);
   }

   public static TexturedModelData getLeftDoubleTexturedModelData() {
      ModelData modelData = new ModelData();
      ModelPartData modelPartData = modelData.getRoot();
      modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F), ModelTransform.NONE);
      modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
      modelPartData.addChild("lock", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -1.0F, 15.0F, 1.0F, 4.0F, 1.0F), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
      return TexturedModelData.of(modelData, 64, 64);
   }

   public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      World world = entity.getWorld();
      boolean bl = world != null;
      BlockState blockState = bl ? entity.getCachedState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
      ChestType chestType = blockState.contains(ChestBlock.CHEST_TYPE) ? (ChestType)blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
      boolean isEnergized = blockState.contains(BlockCircleComponent.ENERGIZED) ? blockState.get(BlockCircleComponent.ENERGIZED) : false;
      Block block = blockState.getBlock();
      // i guess skill issue if we want to add more later,, could just like,, abstract some stuff out to an interface or something i guess
      if (block instanceof BlockSlateChest abstractChestBlock && entity instanceof BlockEntitySlateChest chestEntity) {
         boolean bl2 = chestType != ChestType.SINGLE;
         matrices.push();
         float f = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
         matrices.translate(0.5, 0.5, 0.5);
         matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-f));
         matrices.translate(-0.5, -0.5, -0.5);
         DoubleBlockProperties.PropertySource propertySource;
         if (bl) {
            propertySource = abstractChestBlock.getBlockEntitySource(blockState, world, entity.getPos(), true);
         } else {
            propertySource = DoubleBlockProperties.PropertyRetriever::getFallback;
         }

         float g = ((Float2FloatFunction)propertySource.apply(ChestBlock.getAnimationProgressRetriever((LidOpenable)entity))).get(tickDelta);
         g = 1.0F - g;
         g = 1.0F - g * g * g;
         int i = ((Int2IntFunction)propertySource.apply(new LightmapCoordinatesRetriever())).applyAsInt(light);
         // SpriteIdentifier spriteIdentifier = TexturedRenderLayers.getChestTexture(entity, chestType, false);
         SpriteIdentifier spriteIdentifier = getChestTexture(chestEntity, chestType);
         VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
         if (bl2) {
            if (chestType == ChestType.LEFT) {
               this.render(matrices, vertexConsumer, this.doubleChestLeftLid, this.doubleChestLeftLatch, this.doubleChestLeftBase, g, i, overlay);
            } else {
               this.render(matrices, vertexConsumer, this.doubleChestRightLid, this.doubleChestRightLatch, this.doubleChestRightBase, g, i, overlay);
            }
         } else {
            this.render(matrices, vertexConsumer, this.singleChestLid, this.singleChestLatch, this.singleChestBase, g, i, overlay);
            if(isEnergized){
               matrices.push();
               matrices.translate(0.5f/16f, 0, 0.5f/16f);
               matrices.scale(15f/16f, 0.75f, 15f/16f);
               Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
               // Tessellator tess = Tessellator.getInstance();
               // BufferBuilder buf = tess.getBuffer();
               VertexConsumer buf = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()); // no clue - POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
               Sprite sprite = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("block/white_concrete_powder")).getSprite();
               // buf.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
               for(Direction dir : Direction.values()){
                  if(!dir.getAxis().isHorizontal()) continue;
                  Vec3i normal = dir.getVector();
                  Vec3i nextVec = BERConjuredRedstone.startingVectors.get(dir);
                  // white_concrete_powder
                  for(int d = 0; d < 4; d++){
                     boolean closeToFloor = nextVec.getY() < 0;
                     int color;
                     if(chestEntity.isGloopy()){
                        color = closeToFloor ? 0xA0F2CD : 0x378A6D; // darker gloopy as it goes up  
                     } else {
                        color = !closeToFloor ? 0xb38ef3 : 0xFECBE6; // darker pink as it goes up
                     }
                     buf.vertex(positionMatrix, (nextVec.getX()+1)/2, (nextVec.getY()+1)/2, (nextVec.getZ()+1)/2)
                        .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, closeToFloor ? (int)(0.65*256) : 0)
                        .texture( ((i >> 1) & 1) == 0 ? sprite.getMinU() : sprite.getMaxU(), (((i >> 1) & 1) ^ (i & 1)) == 0 ? sprite.getMinV() : sprite.getMaxV())
                        // .texture( 0,0)
                        .overlay(0,0)
                        .light(255, 255)
                        .normal(normal.getX(), normal.getY(), normal.getZ()) // no clue
                        .next();
                     nextVec = normal.add(normal.crossProduct(nextVec));
                  }
               }
               // RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentShader);
               // RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
               // RenderSystem.enableDepthTest();
               // RenderSystem.enableBlend();
            
               // tess.draw();
               matrices.pop();
            }
         }

         matrices.pop();
      }
   }

   private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
      lid.pitch = -(openFactor * 1.5707964F);
      latch.pitch = lid.pitch;
      lid.render(matrices, vertices, light, overlay);
      latch.render(matrices, vertices, light, overlay);
      base.render(matrices, vertices, light, overlay);
   }

    public SpriteIdentifier getChestTexture(ChestBlockEntity cbe, ChestType type){
        if(cbe instanceof BlockEntitySlateChest slateChest){
            if(slateChest.isGloopy()){
                return getChestTextureId("gloopy_slate_chest");
            } else {
                return getChestTextureId("slate_chest");
            }
        }
        return TexturedRenderLayers.getChestTexture(cbe, type, false);
    }

    public SpriteIdentifier getChestTextureId(String variant){
        // we'll see if this works i guess
        return new SpriteIdentifier(TexturedRenderLayers.CHEST_ATLAS_TEXTURE, new Identifier(HexGloop.MOD_ID, "entity/chest/" + variant));
    }
}

