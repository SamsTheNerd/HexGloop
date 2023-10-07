package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.IShallowMirrorBinder;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;
import com.samsthenerd.hexgloop.utils.ClientUtils;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

@Mixin(HeldItemFeatureRenderer.class)
public class MixinClientTPHandRender {
    @WrapOperation(method="render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/entity/feature/HeldItemFeatureRenderer.renderItem (Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/util/Arm;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void wrapThirdPersonHand(HeldItemFeatureRenderer thisHIFR, LivingEntity entity, ItemStack stack, 
        ModelTransformation.Mode transformationMode, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original){
            // most of this will just get passed through, mostly care about the item and *maybe* vertexconsumer? 
            // although might be better to handle that later so we don't accidentally make an arm green or something
            
            Hand hand = arm == Arm.LEFT ? Hand.OFF_HAND : Hand.MAIN_HAND;
            Pair<ItemStack, Boolean> altStackData = SyncedItemHandling.getAlternateStackClient(entity, hand);
            ItemStack itemToRender = stack;
            if(altStackData != null && ClientUtils.shouldShowReflected()){
                itemToRender = altStackData.getLeft();
                if(altStackData.getRight()){ // is from mirror
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(1));
                } else { // from pedestal
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(2));
                }
            }
            original.call(thisHIFR, entity, itemToRender, transformationMode, arm, matrices, vertexConsumers, light);
    }

    // make sure it renders even when there's nothing in the hand
    @WrapOperation(method="render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V",
    at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isEmpty ()Z"))
    public boolean plzRenderThirdPerson(ItemStack stack, Operation<Boolean> original, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity){
        if(((Object)livingEntity) instanceof IShallowMirrorBinder){
            IShallowMirrorBinder binder = (IShallowMirrorBinder)livingEntity;
            if(!binder.getTrackedStack().isEmpty() && ClientUtils.shouldShowReflected()){
                return false;
            }
        }
        return original.call(stack);
    }

}
