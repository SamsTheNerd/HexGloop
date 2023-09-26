package com.samsthenerd.hexgloop.renderers;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blockentities.BERConjuredRedstone;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;

// vscode just dies if we put <T, M> at the end of this,, i have absolutely no idea why !!!
public class HandThingFeatureRenderer<T extends LivingEntity, M extends EntityModel<?>> extends FeatureRenderer{

    private final BipedEntityModel<LivingEntity> model;
    
    public HandThingFeatureRenderer(FeatureRendererContext<T, EntityModel<T>> context, EntityModelLoader loader, boolean slim) {
        super(context); 
        model = new BipedEntityModel<LivingEntity>(loader.getModelPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER));
        // model = (BipedEntityModel<LivingEntity>)context.getModel(); // unsafe cast !
    }

    private boolean hasSlimArms(PlayerEntity player){
        boolean isSlim = false;
        float slimVal = 1;
        if(player instanceof AbstractClientPlayerEntity cPlayer){
            isSlim = cPlayer.getModel().equals("slim");
            
        }
        return isSlim;
    }

    public void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, PlayerEntity player, BipedEntityModel<LivingEntity> entModel, Arm side, boolean firstPerson){
        matrices.push();
        followBodyRotations(player, entModel);
        if(side == Arm.LEFT){
            translateToLeftArm(matrices, entModel, player, firstPerson);
        } else {
            translateToRightArm(matrices, entModel, player, firstPerson);
        }
        matrices.scale(0.135f, 0.135f, 0.135f); // looks like 0.125 puts it tight to a thick player hand
        // might need to adjust it a bit
        boolean isSlim = hasSlimArms(player);
        float slimVal = (isSlim ? 0.75f : 1f);
        if(isSlim){
                matrices.translate(0.25 * .125 / .135 * (side == Arm.LEFT ? -1 : 1), 0, 0);
            matrices.scale(slimVal, 1, 1);
        }
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        // VertexConsumer buf = vertexConsumers.getBuffer(TexturedRenderLayers.getEntityTranslucentCull()); // no clue - POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        // VertexConsumer buf = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, false)); // no clue - POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL
        // Sprite sprite = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("block/white_concrete_powder")).getSprite();
        // Hex-y pink: #b38ef3
        // bottom of hand

        float handOpacity = 0.75f;
        
        Vec3i firstNormal = Direction.DOWN.getVector();
        buf.vertex(positionMatrix, -1, 0, -1).color(0xb3/256f, 0x8e/256f, 0xf3/256f, handOpacity)
            // .texture( sprite.getMinU(), sprite.getMinV())
            // .overlay(0,0)
            // .light(255, 255)
            // .normal(firstNormal.getX(), firstNormal.getY(), firstNormal.getZ()) // no clue
            .next();
        buf.vertex(positionMatrix, -1, 0, 1).color(0xb3/256f, 0x8e/256f, 0xf3/256f, handOpacity)
            // .texture( sprite.getMinU(), sprite.getMaxV())
            // .overlay(0,0)
            // .light(255, 255)
            // .normal(firstNormal.getX(), firstNormal.getY(), firstNormal.getZ()) // no clue
            .next();
        buf.vertex(positionMatrix, 1, 0, 1).color(0xb3/256f, 0x8e/256f, 0xf3/256f, handOpacity)
            // .texture( sprite.getMaxU(), sprite.getMaxV())
            // .overlay(0,0)
            // .light(255, 255)
            // .normal(firstNormal.getX(), firstNormal.getY(), firstNormal.getZ()) // no clue
            .next();
        buf.vertex(positionMatrix, 1, 0, -1).color(0xb3/256f, 0x8e/256f, 0xf3/256f, handOpacity)
            // .texture( sprite.getMaxU(), sprite.getMinV())
            // .overlay(0,0)
            // .light(255, 255)
            // .normal(firstNormal.getX(), firstNormal.getY(), firstNormal.getZ()) // no clue
            .next();

        for(Direction dir : Direction.values()){
            if(!dir.getAxis().isHorizontal()) continue;
            Vec3i normal = dir.getVector();
            Vec3i nextVec = BERConjuredRedstone.startingVectors.get(dir);
            for(int i = 0; i < 4; i++){
                boolean closeToHand = nextVec.getY() > 0;
                int color = closeToHand ? 0xb38ef3 : 0xFECBE6; // lighter pink as it goes up
                buf.vertex(positionMatrix, nextVec.getX(), (nextVec.getY()-1), nextVec.getZ())
                    .color((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, closeToHand ? (int)(handOpacity*256) : 0)
                    // .texture( ((i >> 1) & 1) == 0 ? sprite.getMinU() : sprite.getMaxU(), (((i >> 1) & 1) ^ (i & 1)) == 0 ? sprite.getMinV() : sprite.getMaxV())
                    // .overlay(0,0)
                    // .light(closeToHand ? 255 : 0, closeToHand ? 255 : 0)
                    // .normal(normal.getX(), normal.getY(), normal.getZ()) // no clue
                    .next();
                nextVec = normal.add(normal.crossProduct(nextVec));
            }
        }

        // RenderSystem.setShader(GameRenderer::getRenderTypeEntityTranslucentShader);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

    
        tess.draw();
        
        matrices.pop();
    }

    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, 
        Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch){
        if(!(entity instanceof PlayerEntity player)) return;
        
        Map<String, List<ItemStack>> trinkets = HexGloop.TRINKETY_INSTANCE.getTrinkets(player);
        boolean hasLeft = false;
        boolean hasRight = false;
        // this is set up for rendering just the glowy effect - would require some reworking to render other trinkets
        for(ItemStack stack : trinkets.getOrDefault("mainhandring", List.of())){
            if(stack.getItem() == HexGloopItems.CASTING_RING_ITEM.get()){
                hasRight = true;
                break;
            }
        }
        for(ItemStack stack : trinkets.getOrDefault("offhandring", List.of())){
            if(stack.getItem() == HexGloopItems.CASTING_RING_ITEM.get()){
                hasLeft = true;
                break;
            }
        }
        if(hasLeft){
            model.setAngles(player, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            model.animateModel(player, limbAngle, limbDistance, tickDelta);
            
            renderArm(matrices, vertexConsumers, player, model, Arm.LEFT, false);
        }
        if(hasRight){
            model.setAngles(player, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
            model.animateModel(player, limbAngle, limbDistance, tickDelta);
            
            renderArm(matrices, vertexConsumers, player, model, Arm.RIGHT, false);
        }
    }

    // guided by / yoinked from artifacts mod

    // idk what 'hasFoil' is
    public final void renderFirstPersonArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, Arm side) {
        if (player.isSpectator()) return;

        Map<String, List<ItemStack>> trinkets = HexGloop.TRINKETY_INSTANCE.getTrinkets(player);
        boolean hasRing = false;
        String handString = side == Arm.LEFT ? "offhandring" : "mainhandring";
        // this is set up for rendering just the glowy effect - would require some reworking to render other trinkets
        for(ItemStack stack : trinkets.getOrDefault(handString, List.of())){
            if(stack.getItem() == HexGloopItems.CASTING_RING_ITEM.get()){
                hasRing = true;
                break;
            }
        }
        if(hasRing){
            ModelPart arm = side == Arm.LEFT ? model.leftArm : model.rightArm;

            model.sneaking = false;
            model.handSwingProgress = model.leaningPitch = 0;
            model.setAngles(player, 0, 0, 0, 0, 0);
            arm.pitch = 0;

            renderArm(matrices, vertexConsumers, player, model, side, true);
        }

        
    }

    // yoinked from TrinketRenderer 
    
    /**
	 * Translates the rendering context to the center of the bottom of the player's right arm
	 */
    static void translateToRightArm(MatrixStack matrices, BipedEntityModel<? extends LivingEntity> model,
			LivingEntity player){ translateToRightArm(matrices, model, player, false);}
	static void translateToRightArm(MatrixStack matrices, BipedEntityModel<? extends LivingEntity> model,
			LivingEntity player, boolean firstPerson) {

		if (player.isInSneakingPose() && !model.riding && !player.isSwimming() && !firstPerson) {
			matrices.translate(0.0F, 0.2F, 0.0F);
		}
		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(model.body.yaw));
		matrices.translate(-0.3125F, 0.15625F, 0.0F);
		matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(model.rightArm.roll));
		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(model.rightArm.yaw));
		matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(model.rightArm.pitch));
		matrices.translate(-0.0625F, 0.625F, 0.0F);
	}

	/**
	 * Translates the rendering context to the center of the bottom of the player's left arm
	 */
    static void translateToLeftArm(MatrixStack matrices, BipedEntityModel<? extends LivingEntity> model,
			LivingEntity player){ translateToLeftArm(matrices, model, player, false);}
	static void translateToLeftArm(MatrixStack matrices, BipedEntityModel<? extends LivingEntity> model,
			LivingEntity player, boolean firstPerson) {

		if (player.isInSneakingPose() && !model.riding && !player.isSwimming() && !firstPerson) {
			matrices.translate(0.0F, 0.2F, 0.0F);
		}
		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(model.body.yaw));
		matrices.translate(0.3125F, 0.15625F, 0.0F);
		matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(model.leftArm.roll));
		matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(model.leftArm.yaw));
		matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(model.leftArm.pitch));
		matrices.translate(0.0625F, 0.625F, 0.0F);
	}

    /**
	 * Rotates the rendering for the models based on the entity's poses and movements. This will do
	 * nothing if the entity render object does not implement {@link LivingEntityRenderer} or if the
	 * model does not implement {@link BipedEntityModel}).
	 *
	 * @param entity The wearer of the trinket
	 * @param model The model to align to the body movement
	 */
	@SuppressWarnings("unchecked")
	static void followBodyRotations(final LivingEntity entity, final BipedEntityModel<LivingEntity> model) {

		EntityRenderer<? super LivingEntity> render = MinecraftClient.getInstance()
				.getEntityRenderDispatcher().getRenderer(entity);

		if (render instanceof LivingEntityRenderer) {
			//noinspection unchecked
			LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer =
					(LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>) render;
			EntityModel<LivingEntity> entityModel = livingRenderer.getModel();

			if (entityModel instanceof BipedEntityModel) {
				BipedEntityModel<LivingEntity> bipedModel = (BipedEntityModel<LivingEntity>) entityModel;
				bipedModel.setAttributes(model);
			}
		}
	}
}
