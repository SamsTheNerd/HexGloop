package com.samsthenerd.hexgloop;

import com.samsthenerd.hexgloop.utils.GloopXPlat;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

public class GloopXPlatFabric implements GloopXPlat {

    // because fabric is normal and doesn't absolutely suck
    public SpawnEggItem getForgeSpawnEggForEntity(EntityType<?> type){
        return null;
    }
}
