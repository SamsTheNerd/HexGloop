package com.samsthenerd.hexgloop.mixins.canvas;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;

@Mixin(ModelLoader.class)
public class MixinClientAddSlateFrameModel {

    @Shadow
    @Final
    private static StateManager<Block, BlockState> ITEM_FRAME_STATE_FACTORY;

    @ModifyExpressionValue(
        method="<clinit>()V",
        at=@At(value="INVOKE", target="com/google/common/collect/ImmutableMap.of (Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap;"),
        remap=false
    )
    private static ImmutableMap<Object, Object> addSlateFrameModel(ImmutableMap<Object, Object> original){
        return ImmutableMap.<Object, Object>builder().putAll(original).put(new Identifier("hexgloop", "slate_frame"), ITEM_FRAME_STATE_FACTORY).build();
    }
}
