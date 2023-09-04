package com.samsthenerd.hexgloop.items;

import java.util.UUID;

import at.petrak.hexcasting.api.addldata.ADColorizer;
import at.petrak.hexcasting.api.item.ColorizerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class ItemHeartPigment extends Item implements ColorizerItem{
    public ItemHeartPigment(Settings properties) {
        super(properties);
    }

    @Override
    public int color(ItemStack stack, UUID owner, float time, Vec3d position) {
        return ADColorizer.morphBetweenColors(getColors(), new Vec3d(0.1, 0.1, 0.1), time / 20 / 20, position);
    }

    public int[] getColors(){
        return new int[]{0x69eb96, 0xeb69d7};
    }

}
