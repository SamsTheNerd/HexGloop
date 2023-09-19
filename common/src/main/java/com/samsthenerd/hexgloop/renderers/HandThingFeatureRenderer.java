package com.samsthenerd.hexgloop.renderers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(EnvType.CLIENT)
public class HandThingFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> 
    extends FeatureRenderer<T, M> {

    private final M model;
    
    public HandThingFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader) {
        super(context); 
        // model = new BipedEntityModel();

        // aeoihukjnvoaieusk
    }
}
