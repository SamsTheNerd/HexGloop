package com.samsthenerd.hexgloop.utils;


import java.util.HashMap;
import java.util.Map;

import at.petrak.hexcasting.api.spell.math.HexDir;

public class StringsToDirMap {
    public static final Map<String, HexDir> dirMap = new HashMap<String, HexDir>();

    public static void init(){
        dirMap.put("northwest", HexDir.NORTH_WEST);
        dirMap.put("west", HexDir.WEST);
        dirMap.put("southwest", HexDir.SOUTH_WEST);
        dirMap.put("southeast", HexDir.SOUTH_EAST);
        dirMap.put("east", HexDir.EAST);
        dirMap.put("northeast", HexDir.NORTH_EAST);
        dirMap.put("nw", HexDir.NORTH_WEST);

        dirMap.put("w", HexDir.WEST);
        dirMap.put("sw", HexDir.SOUTH_WEST);
        dirMap.put("se", HexDir.SOUTH_EAST);
        dirMap.put("e", HexDir.EAST);
        dirMap.put("ne", HexDir.NORTH_EAST);
    }
}
