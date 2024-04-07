package com.samsthenerd.hexgloop.mixins.canvas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.client.gui.PatternTooltipComponent;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(HeldItemRenderer.class)
public class MixinClientHeldCanvasRenderer {
    @WrapOperation(
        method="renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at=@At(value="INVOKE", target="net/minecraft/item/ItemStack.isOf (Lnet/minecraft/item/Item;)Z")
    )
    public boolean canvasIsBasicallyAMapIThink(ItemStack stack, Item item, Operation<Boolean> original){
        if(item == Items.FILLED_MAP && stack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())){
            return true;
        }
        return original.call(stack, item);
    }

    @WrapOperation(
        method="renderFirstPersonMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ItemStack;)V",
        at=@At(value="INVOKE", target="net/minecraft/client/render/VertexConsumerProvider.getBuffer (Lnet/minecraft/client/render/RenderLayer;)Lnet/minecraft/client/render/VertexConsumer;")
    )
    public VertexConsumer makeItSlatey(VertexConsumerProvider provider, RenderLayer originalLayer, Operation<VertexConsumer> original, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack){
        if(stack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())){
            return original.call(provider, RenderLayer.getText(PatternTooltipComponent.SLATE_BG));
        }
        return original.call(provider, originalLayer);
    }
}
