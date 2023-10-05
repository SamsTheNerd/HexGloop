package com.samsthenerd.hexgloop.renderers;

import java.util.function.Function;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class GaslightingVCProvider implements VertexConsumerProvider{
    public VertexConsumerProvider innerProvider;
    Function <VertexConsumer, VertexConsumer> middleConsumerFactory;

    public GaslightingVCProvider(VertexConsumerProvider innerProvider, Function<VertexConsumer, VertexConsumer> middleConsumerFactory){
        this.innerProvider = innerProvider;
        this.middleConsumerFactory = middleConsumerFactory;
    }

    @Override
    public VertexConsumer getBuffer(RenderLayer layer) {
        return middleConsumerFactory.apply(innerProvider.getBuffer(layer));
    }
}
