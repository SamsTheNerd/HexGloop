package com.samsthenerd.hexgloop.renderers.tooltips;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.samsthenerd.hexgloop.items.tooltips.MirrorTooltipData;
import com.samsthenerd.hexgloop.items.tooltips.ScriptTooltipData;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

public class HexGloopTooltips {

    public static final Map<Class<? extends TooltipData>, Function<TooltipData, TooltipComponent>> tooltipDataToComponent = new HashMap<>();

    public static TooltipComponent getTooltipComponent(TooltipData data){
        Function<TooltipData, TooltipComponent> ttFunc = tooltipDataToComponent.get(data.getClass());
        return ttFunc == null ? null : ttFunc.apply(data);
    }

    public static <C extends TooltipComponent, D extends TooltipData> Function<TooltipData, C> convertTooltip(Class<D> dataClass, 
        Function<D, C> componentFactory){
        return (data) -> {
            if(dataClass.isInstance(data)){
                return componentFactory.apply(dataClass.cast(data));
            }
            return null;
        };
    }

    // should be called sided in tooltip registration stuff
    public static void init(){
        tooltipDataToComponent.put(MirrorTooltipData.class, convertTooltip(MirrorTooltipData.class, MirrorTooltipComponent::new));
        tooltipDataToComponent.put(ScriptTooltipData.class, convertTooltip(ScriptTooltipData.class, ScriptTooltipComponent::new));
    }
}
