package com.samsthenerd.hexgloop.mixins.misc;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.renderers.HandThingFeatureRenderer;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinClientAddFeatureRenderers extends LivingEntityRenderer {

    private MixinClientAddFeatureRenderers() {
		super(null, null, 0);
	}

    @Inject(method = "<init>(Lnet/minecraft/client/render/entity/EntityRendererFactory$Context;Z)V",
        at=@At("TAIL"))
    public void addPlayerRenderers(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){
        PlayerEntityRenderer perThis = (PlayerEntityRenderer)(Object)this;
        this.addFeature(new HandThingFeatureRenderer(((FeatureRendererContext)(Object)this), ctx.getModelLoader(), slim));
    }

    @Inject(method="renderLeftArm", at=@At("TAIL"))
    private void renderLeftArmFeature(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, CallbackInfo ci){
        MixinClientExposeAddFeature perThis = (MixinClientExposeAddFeature)(Object)this;
        for(FeatureRenderer fr : perThis.getFeatures()){
            if(fr instanceof HandThingFeatureRenderer handRenderer){
                handRenderer.renderFirstPersonArm(matrices, vertexConsumers, light, player, Arm.LEFT);
            }
        }
    }

    @Inject(method="renderRightArm", at=@At("TAIL"))
    private void renderRightArmFeature(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, CallbackInfo ci){
        MixinClientExposeAddFeature perThis = (MixinClientExposeAddFeature)(Object)this;
        for(FeatureRenderer fr : perThis.getFeatures()){
            if(fr instanceof HandThingFeatureRenderer handRenderer){
                handRenderer.renderFirstPersonArm(matrices, vertexConsumers, light, player, Arm.RIGHT);
            }
        }
    }
}
