package com.samsthenerd.hexgloop.items;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.entity.Entity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

public class ItemSlateCanvas extends FilledMapItem implements IotaHolderItem {
    public ItemSlateCanvas(Settings settings) {
        super(settings);
    }

    // don't do updates
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            MapState mapState = getOrCreateMapState(stack, world); // does this do anything ?
        }
    }

    // just a way to get a new one
    public static Pair<MapState, Integer> createMapState(World world){
        ItemStack normalMapStack = FilledMapItem.createMap(world, 0, 0, (byte) 0, false, false);
        MapState mapState = FilledMapItem.getOrCreateMapState(normalMapStack, world);
        Integer id = FilledMapItem.getMapId(normalMapStack);
        return new Pair<MapState, Integer>(mapState, id);
    }

    public static void setCanvasMapId(ItemStack stack, int mapId){
        stack.getOrCreateNbt().putInt("map", mapId);
    }

    @Override
    @Nullable
    public NbtCompound readIotaTag(ItemStack stack){
        return null;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return iota == null;
    }

    // just using this to allow erasing the map
    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            stack.getNbt().remove("map");
        }
    }
}
