package com.samsthenerd.hexgloop.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;

public class SampleHexes {

    /*
     * regex for translating patterns from logs to here
     * find: HexPattern\((?<dir>[^ ]+) (?<pattern>[^ ]+)\) *
     * replace: new PatternIota(HexPattern.fromAngles("$pattern", HexDir.$dir)),\n
     */
    public static SampleHex PLACE_LIGHT = register(
        new SampleHex(List.of(
            // get block looking at
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("aa", HexDir.EAST)),
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("wa", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("wqaawdd", HexDir.EAST)),
            //
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("aa", HexDir.EAST)),
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("wa", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("weddwaa", HexDir.EAST)),
            // add
            new PatternIota(HexPattern.fromAngles("waaw", HexDir.NORTH_EAST)),
            // put light
            new PatternIota(HexPattern.fromAngles("qqd", HexDir.NORTH_EAST))

        ), Text.translatable("hexgloop.lootitem.name.place_light"), Text.translatable("hexgloop.lootitem.desc.place_light"))
        .forItems(HexGloopItems.SCRIPT_ITEM.get())
        .withRarity(Rarity.UNCOMMON)
    );

    public static SampleHex HOVER = register(
        new SampleHex(List.of(
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("qqqqqew", HexDir.NORTH_WEST)),
            new PatternIota(HexPattern.fromAngles("awqqqwaqw", HexDir.SOUTH_WEST)),
            new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
            new PatternIota(HexPattern.fromAngles("aqaaw", HexDir.SOUTH_EAST)),
            new PatternIota(HexPattern.fromAngles("qqqqqawwawawd", HexDir.WEST))
        ), Text.translatable("hexgloop.lootitem.name.hover"), Text.translatable("hexgloop.lootitem.desc.hover"))
        .forItems(HexGloopItems.SCRIPT_ITEM.get())
        .withRarity(Rarity.RARE)
    );

    private static List<Iota> smeltItems = List.of(
        new PatternIota(HexPattern.fromAngles("qqq", HexDir.WEST)),
        new PatternIota(HexPattern.fromAngles("wqqqwqqadad", HexDir.EAST)),
        new PatternIota(HexPattern.fromAngles("eee", HexDir.EAST)),
        new PatternIota(HexPattern.fromAngles("qaq", HexDir.NORTH_EAST)),
        new PatternIota(HexPattern.fromAngles("dd", HexDir.NORTH_EAST)),
        new PatternIota(HexPattern.fromAngles("aqaawaa", HexDir.SOUTH_EAST)),
        new PatternIota(HexPattern.fromAngles("qqqqqwdeddww", HexDir.SOUTH_EAST)),
        new PatternIota(HexPattern.fromAngles("dadad", HexDir.NORTH_EAST)));

    public static SampleHex LESSER_SMELT = register(
        new SampleHex(smeltItems, Text.translatable("hexgloop.lootitem.name.lesser_smelt"), Text.translatable("hexgloop.lootitem.desc.lesser_smelt"))
        .forItems(HexGloopItems.CASTING_POTION_ITEM.get())
        .withRarity(Rarity.RARE)
        .withMediaAmt(MediaConstants.DUST_UNIT * 48)
        .requiredMods("hexal")
        .withPigments(new FrozenColorizer(HexItems.DYE_COLORIZERS.get(DyeColor.ORANGE).getDefaultStack(), new UUID(0,0)))
    );
    public static SampleHex GREATER_SMELT = register(
        new SampleHex(smeltItems, Text.translatable("hexgloop.lootitem.name.greater_smelt"), Text.translatable("hexgloop.lootitem.desc.greater_smelt"))
        .forItems(HexGloopItems.CASTING_POTION_ITEM.get())
        .withRarity(Rarity.EPIC)
        .withMediaAmt(MediaConstants.DUST_UNIT * 48 * 4)
        .requiredMods("hexal")
        .withPigments(new FrozenColorizer(HexItems.DYE_COLORIZERS.get(DyeColor.RED).getDefaultStack(), new UUID(0,0)))
    );


    public static final Map<Pair<Item, Rarity>, Set<SampleHex>> SAMPLE_HEXES = new HashMap<>();

    public static SampleHex register(SampleHex hex){
        for(Item item : hex.forItems){
            Pair<Item, Rarity> keyPair = new Pair<>(item, hex.rarity);
            if(!SAMPLE_HEXES.containsKey(keyPair)){
                SAMPLE_HEXES.put(keyPair, new HashSet<SampleHex>());
            }
            SAMPLE_HEXES.get(keyPair).add(hex);
        }
        return hex;
    }
}
