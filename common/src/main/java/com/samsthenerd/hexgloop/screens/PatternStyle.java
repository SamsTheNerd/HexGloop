package com.samsthenerd.hexgloop.screens;

import java.util.List;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import net.minecraft.text.Style;
import net.minecraft.util.math.Vec2f;

// put this on text (maybe a specific character, tbd) and it'll render it as a hex pattern
public interface PatternStyle {

    public HexPattern getPattern();

    // note that style is meant to be immutable and this mutates it
    public Style setPattern(HexPattern pattern);

    public Style withPattern(HexPattern pattern);

    public static Style fromPattern(HexPattern pattern){
        return ((PatternStyle)Style.EMPTY.withBold(null)).setPattern(pattern); // just to get an empty style
    }

    // mimic tooltip rendering
    public List<Vec2f> getZappyPoints();

    public List<Vec2f> getPathfinderDots();
}
