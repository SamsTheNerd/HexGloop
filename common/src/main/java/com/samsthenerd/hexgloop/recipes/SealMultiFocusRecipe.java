package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemMultiFocus;

import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.items.ItemSpellbook;
import at.petrak.hexcasting.common.lib.HexItems;
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
        if(craftingInventory.count(Items.HONEYCOMB) == 0){
            return false;
        }

        boolean foundOne = false;
        Item foundItem = null;
        // check for dyeable spellbook first, it's not great but we're tacking it on here !
        if(craftingInventory.count(HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get()) == 1){
            if(foundOne) return false;
            foundOne = true;
            foundItem = HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get();
        }
        for(int i = 0; i < HexGloop.FOCUS_ITEMS.size(); i++){
            if(craftingInventory.count(HexGloop.FOCUS_ITEMS.get(i).get()) == 1){
                if(foundOne) return false;
                foundOne = true;
                foundItem = HexGloop.FOCUS_ITEMS.get(i).get();
            }
        }
        if(!foundOne) return false;

        for(int s = 0; s < craftingInventory.size(); s++){
            ItemStack stack = craftingInventory.getStack(s);
            if(stack.getItem() == foundItem){
                if(foundItem instanceof ItemMultiFocus){
                    if(!ItemSpellbook.isSealed(stack) && HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack) != null){
                        return true;
                    }
                } else if(stack.getItem() == HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get()){
                    if(!ItemSpellbook.isSealed(stack) && HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get().readIotaTag(stack) != null){
                        return true;
                    }
                } else {
                    if(!ItemFocus.isSealed(stack) && HexItems.FOCUS.readIotaTag(stack) != null){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        for(int s = 0; s < craftingInventory.size(); s++){
            ItemStack stack = craftingInventory.getStack(s);
            if(stack.getItem() instanceof ItemMultiFocus || stack.getItem() instanceof ItemSpellbook){
                ItemStack returnStack = stack.copy();
                ItemSpellbook.setSealed(returnStack, true);
                return returnStack;
            } else if(stack.getItem() instanceof ItemFocus){
                ItemStack returnStack = stack.copy();
                returnStack.getNbt().putBoolean(ItemFocus.TAG_SEALED, true);
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
