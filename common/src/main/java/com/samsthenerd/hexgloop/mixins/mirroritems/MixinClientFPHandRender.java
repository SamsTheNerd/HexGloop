package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;
import com.samsthenerd.hexgloop.utils.ClientUtils;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;

@Mixin(HeldItemRenderer.class)
public class MixinClientFPHandRender {
    @WrapOperation(method="renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/item/HeldItemRenderer.renderFirstPersonItem (Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    public void wrapFirstPersonHand(HeldItemRenderer thisHIR, AbstractClientPlayerEntity player, float tickDelta, 
        float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original){
            // most of this will just get passed through, mostly care about the item and *maybe* vertexconsumer? 
            // although might be better to handle that later so we don't accidentally make an arm green or something
            Pair<ItemStack, Boolean> altStackData = SyncedItemHandling.getAlternateStackClient(player, hand);
            ItemStack itemToRender = item;
            if(altStackData != null && ClientUtils.shouldShowReflected()){
                itemToRender = altStackData.getLeft();
                if(altStackData.getRight()){ // is from mirror
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(1));
                } else { // from pedestal
                    itemToRender.setSubNbt(SyncedItemHandling.IS_REFLECTED_TAG, NbtDouble.of(2));
                }
            }
            original.call(thisHIR, player, tickDelta, pitch, hand, swingProgress, itemToRender, equipProgress, matrices, vertexConsumers, light);
        }
}
