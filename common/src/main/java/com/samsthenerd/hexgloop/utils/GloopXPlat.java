package com.samsthenerd.hexgloop.utils;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

public interface GloopXPlat {
    // i hate forge
    public SpawnEggItem getForgeSpawnEggForEntity(EntityType<?> type);
}
