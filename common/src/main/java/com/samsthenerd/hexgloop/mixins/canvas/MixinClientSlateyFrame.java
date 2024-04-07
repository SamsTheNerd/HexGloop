package com.samsthenerd.hexgloop.mixins.canvas;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import net.minecraft.client.render.entity.ItemFrameEntityRenderer;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;

@Mixin(ItemFrameEntityRenderer.class)
public class MixinClientSlateyFrame {
    @Inject(
        method="getModelId(Lnet/minecraft/entity/decoration/ItemFrameEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/util/ModelIdentifier;",
        at=@At("HEAD"),
        cancellable=true
    )
    public void makeItSlatey(ItemFrameEntity entity, ItemStack stack, CallbackInfoReturnable<ModelIdentifier> cir){
        if(stack.isOf(HexGloopItems.SLATE_CANVAS_ITEM.get())){
            cir.setReturnValue(new ModelIdentifier("hexgloop:slate_frame", "map=true"));
        }
    }
}
