package com.samsthenerd.hexgloop.mixins.misc;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;

@Mixin(LivingEntityRenderer.class)
public interface MixinClientExposeAddFeature {
    @Invoker("addFeature")
    boolean invokeAddFeature(FeatureRenderer feature);

    @Accessor("features")
    List<FeatureRenderer> getFeatures();
}
