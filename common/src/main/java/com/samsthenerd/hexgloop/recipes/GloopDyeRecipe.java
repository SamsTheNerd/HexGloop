package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemGloopDye;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

// maybe not the neatest for mod compat but whatever, it's probably good enough for most things
public class GloopDyeRecipe extends SpecialCraftingRecipe{
    public static final SpecialRecipeSerializer<GloopDyeRecipe> SERIALIZER =
        new SpecialRecipeSerializer<GloopDyeRecipe>(GloopDyeRecipe::new);

    public GloopDyeRecipe(Identifier identifier) {
        super(identifier);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack gloopDyeStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.getItem() instanceof DyeableItem) {
                if (!itemStack.isEmpty()) {
                    return false;
                }
                itemStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() instanceof ItemGloopDye) {
                if (!gloopDyeStack.isEmpty()) {
                    return false;
                }
                if(HexGloopItems.GLOOP_DYE_ITEM.get().getMedia(gloopDyeStack) < 1)
                    return false;
                gloopDyeStack = itemStack2;
                continue;
            }
            return false;
        }
        return !itemStack.isEmpty() && !gloopDyeStack.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack itemStack = ItemStack.EMPTY;
        ItemStack gloopDyeStack = ItemStack.EMPTY;
        for (int i = 0; i < craftingInventory.size(); ++i) {
            ItemStack itemStack2 = craftingInventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            Item item = itemStack2.getItem();
            if (item instanceof DyeableItem) {
                if (!itemStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                itemStack = itemStack2.copy();
                continue;
            }
            if (item instanceof ItemGloopDye) {
                if (!gloopDyeStack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                gloopDyeStack = itemStack2.copy();
                continue;
            }
            return ItemStack.EMPTY;
        }
        if (itemStack.isEmpty() || gloopDyeStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ((DyeableItem) itemStack.getItem()).setColor(itemStack, ItemGloopDye.getDyeColor(gloopDyeStack));
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();
            if(item instanceof ItemGloopDye){
                ItemStack smallerGloop = inventory.getStack(i).copy();
                HexGloopItems.GLOOP_DYE_ITEM.get().decrementMedia(smallerGloop, 1);
            }
            if (!item.hasRecipeRemainder()) continue;
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }
}
