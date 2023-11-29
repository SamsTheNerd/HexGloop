package com.samsthenerd.hexgloop.loot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class SampleHex{
    public Rarity rarity = Rarity.COMMON;

    public List<Iota> patterns;
    public Text name;
    public @Nullable Text desc;

    // null if anywhere normal
    public Set<Identifier> locations = null;
    // air if it can be spawned on any item
    public Set<? extends Item> forItems = Set.of(Items.AIR);
    public Set<FrozenColorizer> pigments = null;
    public int mediaAmt = MediaConstants.SHARD_UNIT * 8;

    public Set<String> mods = new HashSet<>();

    public SampleHex(List<Iota> patterns, Text name, @Nullable Text desc){
        this.patterns = patterns;
        this.name = name;
        this.desc = desc;
    }

    public SampleHex withRarity(Rarity rarity){
        this.rarity = rarity;
        return this;
    }

    // if it should only spawn in specific places
    public SampleHex withLocations(Set<Identifier> locations){
        this.locations = locations;
        return this;
    }

    public SampleHex withLocations(Identifier... locations){
        this.locations = Set.of(locations);
        return this;
    }

    public SampleHex forItems(Set<? extends Item> itemTypes){
        forItems = itemTypes;
        return this;
    }

    public SampleHex forItems(Item... itemTypes){
        forItems = Set.of(itemTypes);
        return this;
    }

    public SampleHex withPigments(Set<FrozenColorizer> pigments){
        this.pigments = pigments;
        return this;
    }

    public SampleHex withPigments(FrozenColorizer... pigments){
        this.pigments = Set.of(pigments);
        return this;
    }
    
    public SampleHex withMediaAmt(int amt){
        this.mediaAmt = amt;
        return this;
    }

    public SampleHex requiredMods(String... modIDs){
        mods = Set.of(modIDs);
        return this;
    }
}

