package com.samsthenerd.hexgloop.forge;

import java.util.Map.Entry;
import java.util.function.Function;

import com.samsthenerd.hexgloop.renderers.tooltips.HexGloopTooltips;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HexGloopForgeClient {
    @SubscribeEvent
    public void registerTooltipComponents(RegisterClientTooltipComponentFactoriesEvent evt) {
        HexGloopTooltips.init();
        for(Entry<Class<? extends TooltipData>, Function<TooltipData, TooltipComponent>> entry : HexGloopTooltips.tooltipDataToComponent.entrySet()){
            evt.register(entry.getKey(), entry.getValue());
        }
    }
}
