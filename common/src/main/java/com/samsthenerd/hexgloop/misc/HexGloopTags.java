package com.samsthenerd.hexgloop.misc;

import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopTags {
    // for if it shouldn't pass through to the circle
    public static final TagKey<Item> NOT_PATTERN_PEDESTAL_PROVIDER = TagKey.of(Registry.ITEM_KEY, 
        new Identifier(HexGloop.MOD_ID, "pedestal_pattern_item_blacklist"));
}
