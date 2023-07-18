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

    public default Style withPattern(HexPattern pattern){
        return withPattern(pattern, true, true);
    }

    // in case you don't want the angle sigs / larger render to show on hover/click for whatever reason
    public Style withPattern(HexPattern pattern, boolean withPatternHoverEvent, boolean withPatternClickEvent);

    public static Style fromPattern(HexPattern pattern){
        return ((PatternStyle)Style.EMPTY.withBold(null)).setPattern(pattern); // just to get an empty style
    }

    // mimic tooltip rendering
    public List<Vec2f> getZappyPoints();

    public List<Vec2f> getPathfinderDots();

    
}
