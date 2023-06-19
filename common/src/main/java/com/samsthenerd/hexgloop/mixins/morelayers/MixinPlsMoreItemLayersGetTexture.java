package com.samsthenerd.hexgloop.mixins.morelayers;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.model.json.JsonUnbakedModel;

@Mixin(JsonUnbakedModel.class)
public class MixinPlsMoreItemLayersGetTexture {
    //     @Redirect(method = "getTextureDependencies(Ljava/util/function/Function;Ljava/util/Set;)Ljava/util/Collection;", 
    //     at = @At(value = "FIELD", 
    //     target = "Lnet/minecraft/client/render/model/json/ItemModelGenerator;LAYERS:Ljava/util/List;", opcode = Opcodes.GETSTATIC))
    // public List<String> getLongerLayersInTexture() {
    //     return MoreModelLayers.MORE_LAYERS;
    // }
}
