package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemMultiFocus;

import at.petrak.hexcasting.common.items.ItemSpellbook;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class SealMultiFocusRecipe extends SpecialCraftingRecipe{
    public static final SpecialRecipeSerializer<SealMultiFocusRecipe> SERIALIZER =
        new SpecialRecipeSerializer<SealMultiFocusRecipe>(SealMultiFocusRecipe::new);

    public SealMultiFocusRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        HexGloop.logPrint("trying match to seal multi focus");
        if(craftingInventory.count(Items.HONEYCOMB) == 0){
            return false;
        }
        if(craftingInventory.count(HexGloopItems.MULTI_FOCUS_ITEM.get()) != 1)
            return false;

        HexGloop.logPrint("right number of items");
        for(int s = 0; s < craftingInventory.size(); s++){
            ItemStack stack = craftingInventory.getStack(s);
            if(stack.getItem() instanceof ItemMultiFocus){
                if(!ItemSpellbook.isSealed(stack) && HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack) != null){
                    return true;
                }
            }
        }
        HexGloop.logPrint("no unsealed multi focus");
        return false;
    }


    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        for(int s = 0; s < craftingInventory.size(); s++){
            ItemStack stack = craftingInventory.getStack(s);
            if(stack.getItem() instanceof ItemMultiFocus){
                ItemStack returnStack = stack.copy();
                ItemSpellbook.setSealed(returnStack, true);
                return returnStack;
            }
        }
        return ItemStack.EMPTY;
    }

    public DefaultedList<ItemStack> getRemainder(CraftingInventory inventory) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.ofSize(inventory.size(), ItemStack.EMPTY);
        for (int i = 0; i < defaultedList.size(); ++i) {
            Item item = inventory.getStack(i).getItem();
            if (!item.hasRecipeRemainder()) continue;
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }

    // @Override
    // public ItemStack getOutput() {
    //     return new ItemStack(DuckyPeriph.KEYBOARD_ITEM);
    // }

    // can't reach much from 2x2 but eh
    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return false;
    }
}
