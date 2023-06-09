package com.samsthenerd.hexgloop.mixins.morelayers;

import java.util.List;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.misc.wnboi.MoreModelLayers;

import net.minecraft.client.render.model.json.JsonUnbakedModel;

@Mixin(JsonUnbakedModel.class)
public class MixinPlsMoreItemLayersGetTexture {


        @Redirect(method = "getTextureDependencies(Ljava/util/function/Function;Ljava/util/Set;)Ljava/util/Collection;", 
        at = @At(value = "FIELD", 
        target = "Lnet/minecraft/client/render/model/json/ItemModelGenerator;LAYERS:Ljava/util/List;", opcode = Opcodes.GETSTATIC))
    public List<String> getLongerLayersInTexture() {
        return MoreModelLayers.MORE_LAYERS;
    }
}
