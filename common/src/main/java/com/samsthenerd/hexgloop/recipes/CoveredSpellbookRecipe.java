package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.misc.HexGloopTags;

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

public class CoveredSpellbookRecipe extends SpecialCraftingRecipe{
    public static final SpecialRecipeSerializer<CoveredSpellbookRecipe> SERIALIZER =
        new SpecialRecipeSerializer<CoveredSpellbookRecipe>(CoveredSpellbookRecipe::new);

    public CoveredSpellbookRecipe(Identifier identifier) {
        super(identifier);
    }

    protected static boolean isLeatherIsh(ItemStack stack){
        return stack.isIn(HexGloopTags.FABRIC_LEATHERY) || stack.isIn(HexGloopTags.FORGE_LEATHERY) || stack.getItem() == Items.LEATHER;
    }

    @Override
    public boolean matches(CraftingInventory craftingInventory, World world) {
        int bookslot = -1;
        for(int i = 0; i < craftingInventory.getHeight()*craftingInventory.getWidth(); i++){
            if(craftingInventory.getStack(i).getItem() == HexItems.SPELLBOOK){
                if(bookslot != -1){
                    return false;
                }
                bookslot = i;
            }
        }
        if(bookslot == -1){
            return false;
        }
        int bookx = bookslot % craftingInventory.getWidth();
        int booky = bookslot / craftingInventory.getWidth();
        // get the leather-y pieces
        boolean gotNetherite = false;
        int leatherCount = 0;
        for(int i = 0; i < craftingInventory.getHeight()*craftingInventory.getWidth(); i++){
            if(i == bookslot) continue; // don't worry about that
            int slotx = i % craftingInventory.getWidth();
            int sloty = i / craftingInventory.getWidth();
            boolean closeToBook = Math.abs(slotx - bookx) <= 1 && Math.abs(sloty - booky) <= 1; // if it's adjacent/diagonal
            if(slotx == bookx && sloty == booky+1){
                if(craftingInventory.getStack(i).getItem() != Items.NETHERITE_INGOT){
                    return false;
                }
                gotNetherite = true;
                continue;
            }
            if(closeToBook){
                if(!isLeatherIsh(craftingInventory.getStack(i)))
                    return false;
                leatherCount++;
                continue;
            }
            if(!closeToBook && !craftingInventory.getStack(i).isEmpty()){
                return false;
            }
        }
        return gotNetherite && leatherCount == 7;
    }

    @Override
    public ItemStack craft(CraftingInventory craftingInventory) {
        ItemStack oldSpellbook = ItemStack.EMPTY;
        for(int i = 0; i < craftingInventory.getHeight()*craftingInventory.getWidth(); i++){
            if(craftingInventory.getStack(i).getItem() == HexItems.SPELLBOOK){
                oldSpellbook = craftingInventory.getStack(i);
            }
        }
        // it shouldn't be but just in case
        if(oldSpellbook.isEmpty()){
            return ItemStack.EMPTY;
        }
        ItemStack newSpellbook = new ItemStack(HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get());
        newSpellbook.setNbt(oldSpellbook.getNbt().copy());
        return newSpellbook;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 3 && height >= 3;
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
            if(item instanceof ItemSpellbook) continue;
            if (!item.hasRecipeRemainder()) continue;
            defaultedList.set(i, new ItemStack(item.getRecipeRemainder()));
        }
        return defaultedList;
    }
}

