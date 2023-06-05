package com.samsthenerd.hexgloop.mixins;

import java.util.List;
import java.util.function.Function;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.samsthenerd.hexgloop.misc.wnboi.MoreModelLayers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;


@Environment(value=EnvType.CLIENT)
@Mixin(ItemModelGenerator.class)
public class MixinPlsMoreItemLayers {
    

    // why is this method signature so long :(
    @Redirect(method = "create(Ljava/util/function/Function;Lnet/minecraft/client/render/model/json/JsonUnbakedModel;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", 
        at = @At(value = "FIELD", 
        target = "Lnet/minecraft/client/render/model/json/ItemModelGenerator;LAYERS:Ljava/util/List;", opcode = Opcodes.GETSTATIC))
    public List<String> getLongerLayers(Function<SpriteIdentifier, Sprite> textureGetter, JsonUnbakedModel blockModel) {
        return MoreModelLayers.MORE_LAYERS;
    }
}
