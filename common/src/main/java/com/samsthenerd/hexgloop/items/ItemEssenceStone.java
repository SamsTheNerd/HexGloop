package com.samsthenerd.hexgloop.items;

import java.util.UUID;

import javax.annotation.Nullable;

import at.petrak.hexcasting.api.addldata.ADColorizer;
import at.petrak.hexcasting.api.item.ColorizerItem;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;

public class ItemEssenceStone extends Item implements ColorizerItem, IotaHolderItem{

    public static Identifier ESSENCE_PREDICATE = new Identifier("hexgloop", "has_essence");
    public static String HAS_ESSENCE = "has_essence";
    public static String PRIMARY_COLOR = "primary_color";
    public static String SECONDARY_COLOR = "secondary_color";
    public static final String TAG_DATA = "data";

    public ItemEssenceStone(Settings settings){
        super(settings);
    }

    public int color(ItemStack stack, UUID owner, float time, Vec3d position){
        Pair<Integer, Integer> colors = getColors(stack);
        int[] colorsArray = {colors.getLeft(), colors.getRight()};
        return ADColorizer.morphBetweenColors(colorsArray, new Vec3d(0.1, 0.1, 0.1), time / 20 / 20, position);
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        return true;
    }

    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            clearEssence(stack);
            return;
        }
        NBTHelper.put(stack, TAG_DATA, HexIotaTypes.serialize(iota));
        if(iota instanceof EntityIota eIota){
            Entity ent = eIota.getEntity();
            SpawnEggItem spawnEgg = SpawnEggItem.forEntity(ent.getType());
            if(spawnEgg != null){
                int primaryColor = spawnEgg.getColor(0);
                int secondaryColor = spawnEgg.getColor(1);
                setColors(stack, primaryColor, secondaryColor);
                return;
            }
        }
        int iotaColor = iota.getType().color();
        setColors(stack, iotaColor, iotaColor);
    }

    public NbtCompound readIotaTag(ItemStack stack){
        return null; // i don't really want it to be a focus
        // return NBTHelper.getCompound(stack, TAG_DATA);
    }

    public void clearEssence(ItemStack stack){
        NbtCompound nbt = stack.getNbt();
        if(nbt != null && nbt.contains(HAS_ESSENCE, NbtElement.INT_TYPE)){
            nbt.putInt(HAS_ESSENCE, 0);
        }
    }

    public boolean hasEssence(ItemStack stack){
        NbtCompound nbt = stack.getNbt();
        if(nbt != null && nbt.contains(HAS_ESSENCE, NbtElement.INT_TYPE)){
            return nbt.getInt(HAS_ESSENCE) == 1;
        }
        return false;
    }

    public Pair<Integer, Integer> getColors(ItemStack stack){
        NbtCompound nbt = stack.getNbt();
        if(hasEssence(stack) && nbt != null && nbt.contains(PRIMARY_COLOR, NbtElement.INT_TYPE) 
        && nbt.contains(SECONDARY_COLOR, NbtElement.INT_TYPE)){
            return new Pair<Integer, Integer>(nbt.getInt(PRIMARY_COLOR), nbt.getInt(SECONDARY_COLOR));
        }
        return new Pair<Integer, Integer>(0x564661, 0x564661);
    }

    public void setColors(ItemStack stack, int primary, int secondary){
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putInt(PRIMARY_COLOR, primary);
        nbt.putInt(SECONDARY_COLOR, secondary);
        nbt.putInt(HAS_ESSENCE, 1);
        stack.setNbt(nbt);
    }
}
