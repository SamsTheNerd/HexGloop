package com.samsthenerd.hexgloop.misc;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;

public class GloopBanners {
    public static final TagKey<BannerPattern> HERMES_PATTERN_ITEM_KEY = TagKey.of(RegistryKeys.BANNER_PATTERN, new Identifier(HexGloop.MOD_ID, "pattern_item/hermes"));
    public static final RegistryKey<BannerPattern> BANNER_PATTERNS = HexGloop.REGISTRIES.get().tryCast(RegistryKeys.BANNER_PATTERN).get();

    public static void registerBannerPatterns(){
        Registry.register(Registries.BANNER_PATTERN, new Identifier(HexGloop.MOD_ID, "hermes"), new BannerPattern("hermes"));
    }
}
