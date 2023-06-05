package com.samsthenerd.hexgloop.misc.wnboi;

import java.util.List;

import com.google.common.collect.Lists;

public class MoreModelLayers {
    public static final List<String> MORE_LAYERS = genLayers();

    private static List<String> genLayers(){
        List<String> layers = Lists.newArrayList();
        for (int i = 0; i < 32; i++) {
            String layer = "layer" + i;
            layers.add(layer);
        }
        return layers;
    }
}
