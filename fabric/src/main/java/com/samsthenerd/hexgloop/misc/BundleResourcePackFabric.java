package com.samsthenerd.hexgloop.misc;

import com.samsthenerd.hexgloop.HexGloop;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class BundleResourcePackFabric {
    // yoinked from https://github.com/TeamMidnightDust/BetterBeds/blob/6be616e91eea01dba91c842bd5a5bd6693991bba/src/main/java/eu/midnightdust/betterbeds/BetterBedsClient.java#L4
    public static void register(){
        FabricLoader.getInstance().getModContainer(HexGloop.MOD_ID).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier(HexGloop.MOD_ID, "dyeablestaffs"),  modContainer, ResourcePackActivationType.NORMAL);
        });
    }
}
