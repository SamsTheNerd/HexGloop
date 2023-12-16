package com.samsthenerd.hexgloop.misc;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class GloopBanners {
    public static final TagKey<BannerPattern> HERMES_PATTERN_ITEM_KEY = TagKey.of(Registry.BANNER_PATTERN_KEY, new Identifier(HexGloop.MOD_ID, "pattern_item/hermes"));
    public static final Registrar<BannerPattern> BANNER_PATTERNS = HexGloop.REGISTRIES.get().get(Registry.BANNER_PATTERN_KEY);

    public static void registerBannerPatterns(){
        BANNER_PATTERNS.register(new Identifier(HexGloop.MOD_ID, "hermes"), () -> new BannerPattern("hermes"));
    }
}
