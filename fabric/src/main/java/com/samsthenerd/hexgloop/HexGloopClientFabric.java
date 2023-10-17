package com.samsthenerd.hexgloop;

import com.samsthenerd.hexgloop.renderers.tooltips.HexGloopTooltips;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;

public class HexGloopClientFabric implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        HexGloopClient.onInitializeClient();
        
        HexGloopTooltips.init();
        TooltipComponentCallback.EVENT.register(HexGloopTooltips::getTooltipComponent);
    }
}
