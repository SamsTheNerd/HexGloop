package com.samsthenerd.hexgloop.items.tooltips;

import java.util.List;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.client.item.TooltipData;
import net.minecraft.util.Identifier;

public class ScriptTooltipData implements TooltipData{
    public List<HexPattern> patterns;
    public Identifier background;

    public ScriptTooltipData(List<HexPattern> patterns, Identifier background){
        this.patterns = patterns;
        this.background = background;
    }
}
