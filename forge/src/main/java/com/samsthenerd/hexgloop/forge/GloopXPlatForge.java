package com.samsthenerd.hexgloop.forge;

import com.samsthenerd.hexgloop.utils.GloopXPlat;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;

public class GloopXPlatForge implements GloopXPlat {
    public SpawnEggItem getForgeSpawnEggForEntity(EntityType<?> type){
        return ForgeSpawnEggItem.fromEntityType(type);
    }
}
