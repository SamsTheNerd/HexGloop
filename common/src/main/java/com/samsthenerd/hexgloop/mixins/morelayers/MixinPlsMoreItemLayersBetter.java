package com.samsthenerd.hexgloop.mixins.morelayers;

import java.util.ArrayList;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Lists;

import net.minecraft.client.render.model.json.ItemModelGenerator;

@Mixin(ItemModelGenerator.class)
public class MixinPlsMoreItemLayersBetter {
    @Redirect(method="<clinit>", at=@At(value="INVOKE", opcode = Opcodes.INVOKESTATIC, 
        target = "com/google/common/collect/Lists.newArrayList ([Ljava/lang/Object;)Ljava/util/ArrayList;"))
    private static ArrayList<String> getLongerLayers(Object[] object) {
        ArrayList<String> layers = Lists.newArrayList();
        for (int i = 0; i < 32; i++) {
            String layer = "layer" + i;
            layers.add(layer);
        }
        return layers;
    }
}
