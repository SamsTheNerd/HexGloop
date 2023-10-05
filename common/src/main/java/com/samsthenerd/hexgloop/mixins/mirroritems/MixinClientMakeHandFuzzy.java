package com.samsthenerd.hexgloop.mixins.mirroritems;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;
import com.samsthenerd.hexgloop.renderers.GaslightingVCProvider;
import com.samsthenerd.hexgloop.renderers.MiddleVertexConsumer;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

@Mixin(ItemRenderer.class)
public class MixinClientMakeHandFuzzy {
    private VertexConsumer makeNewConsumer(VertexConsumer oldConsumer, boolean isHandMirror){
        return new MiddleVertexConsumer(oldConsumer){
                @Override
                // i'm like pretty sure that this color isn't *actually* used, but we'll see
                public VertexConsumer color(int red, int green, int blue, int alpha){
                    int rgb = isHandMirror ? 0xA0F2CD : 0x378A6D; // darker if it's from a pedestal
                    return super.color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, isHandMirror ? 192 : 64);
                }
            };
    }

    @WrapOperation(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/item/ItemRenderer.renderBakedItemModel (Lnet/minecraft/client/render/model/BakedModel;Lnet/minecraft/item/ItemStack;IILnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;)V"))
    public void gaslightBakedModelRenderer(ItemRenderer thisIR, BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices, Operation<Void> original){
        NbtCompound nbt = stack.getNbt();
        VertexConsumer newVertices = vertices;
        if(nbt != null && nbt.contains(SyncedItemHandling.IS_REFLECTED_TAG, NbtElement.DOUBLE_TYPE)){
            double isReflected = nbt.getDouble(SyncedItemHandling.IS_REFLECTED_TAG);
            if(isReflected == 1){
                // from hand mirror
                newVertices = makeNewConsumer(vertices, true);
            } else if(isReflected == 2){
                // from pedestal
                newVertices = makeNewConsumer(vertices, false);
            }
        }
        original.call(thisIR, model, stack, light, overlay, matrices, newVertices);
    }

    @WrapOperation(method="renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
    at=@At(value="INVOKE", target="net/minecraft/client/render/item/BuiltinModelItemRenderer.render (Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V"))
    public void gaslightBuiltInRenderer(BuiltinModelItemRenderer modelItemRenderer, ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Operation<Void> original){
        NbtCompound nbt = stack.getNbt();
        VertexConsumerProvider newProvider = vertexConsumers;
        if(nbt != null && nbt.contains(SyncedItemHandling.IS_REFLECTED_TAG, NbtElement.DOUBLE_TYPE)){
            double isReflected = nbt.getDouble(SyncedItemHandling.IS_REFLECTED_TAG);
            if(isReflected == 1){
                // from hand mirror
                newProvider = new GaslightingVCProvider(vertexConsumers, (vc) -> makeNewConsumer(vc, true));
            } else if(isReflected == 2){
                // from pedestal
                newProvider = new GaslightingVCProvider(vertexConsumers, (vc) -> makeNewConsumer(vc, false));
            }
        }
        original.call(modelItemRenderer, stack, mode, matrices, newProvider, light, overlay);
    }
}
