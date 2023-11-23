package com.samsthenerd.hexgloop.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class ItemLibraryCard extends Item {

    public static String TAG_DIMENSION = "bound_dimension";
    public static String DIMENSION_PREDICATE = "hexgloop:dimension";

    public ItemLibraryCard(Settings settings) {
        super(settings);
    }

    public void setDimension(ItemStack stack, RegistryKey<World> dim){
        stack.getOrCreateNbt().putString(TAG_DIMENSION, dim.getValue().toString());
    }

    @Nullable
    public RegistryKey<World> getDimension(ItemStack stack){
        NbtCompound nbt = stack.getNbt();
        if(nbt == null || !nbt.contains(TAG_DIMENSION, NbtElement.STRING_TYPE))
            return null;
        return RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString(TAG_DIMENSION)));
    }

    public static final List<RegistryKey<World>> DIMENSIONS = List.of(World.OVERWORLD, World.NETHER, World.END);

    public float getPredicateValue(RegistryKey<World> dim){
        if(dim == null)
            return 0;

        return 1;
    }
}
