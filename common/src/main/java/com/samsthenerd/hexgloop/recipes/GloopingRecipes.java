package com.samsthenerd.hexgloop.recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Pair;

// there shouldn't be too many of these? prob more convenient than using mc recipe system? 
// possible todo to use that though for data pack support
public class GloopingRecipes {
    public static List<GloopingRecipe> RECIPES = new ArrayList<GloopingRecipe>();

    // register recipes here - sort it by priority if you add them anywhere else
    public static void init(){
        // RECIPES.add(new SimpleGloopingRecipe(() -> List.of(
        //     new Pair<>(3, Items.SLIME_BALL),
        //     new Pair<>(1, Items.AMETHYST_SHARD),
        //     new Pair<>(3, HexItems.CHARGED_AMETHYST)
        //     ), () -> HexGloopItems.GLOOP_ITEM.get().getDefaultStack(), 5));
        // RECIPES.add(new SimpleGloopingRecipe(() -> List.of(
        //     new Pair<>(3, Items.SLIME_BALL),
        //     new Pair<>(1, Items.ENDER_EYE),
        //     new Pair<>(3, HexItems.CHARGED_AMETHYST)
        //     ), () -> HexGloopItems.SYNCHRONOUS_GLOOP_ITEM.get().getDefaultStack(), 5));
        
        RECIPES.sort((a, b) -> a.getPriority() - b.getPriority());
    }

    public static GloopingRecipe findRecipe(List<Entity> inputs, RecipeManager recipeManager){
        // almost certainly not efficient for large amounts of recipes, but there won't be many and idc enough to learn how to have it react to data
        List<GloopingRecipe> allRecipes = new ArrayList<>(recipeManager.listAllOfType(DataGloopingRecipe.Type.INSTANCE));
        allRecipes.addAll(RECIPES);
        allRecipes.sort((a, b) -> a.getPriority() - b.getPriority());
        for(GloopingRecipe recipe : allRecipes){
            if(recipe.matches(inputs)){
                return recipe;
            }
        }
        return null;
    }

    public interface GloopingRecipe{
        public boolean matches(List<Entity> inputs);
        
        // only call this if matches is true
        public ItemStack craft(List<Entity> inputs, boolean forRealzies);

        // more of a preview
        public ItemStack getOutput();

        public int getPriority();

        public int getMediaCost();
    }

    public record SimpleGloopingRecipe(Supplier<List<Pair<Integer, Item>>> ingredients, Supplier<ItemStack> output, int priority, int cost) implements GloopingRecipe{
        public boolean matches(List<Entity> inputs) {
            List<Pair<Integer, Item>> remainingIngredients = new ArrayList<>(ingredients.get());
            for(Entity ent : inputs){
                if(ent instanceof ItemEntity itemEnt){
                    ItemStack stack = itemEnt.getStack();
                    if(stack.isEmpty())
                        continue;
                    for(int i = 0; i < remainingIngredients.size(); i++){
                        Pair<Integer, Item> ingredient = remainingIngredients.get(i);
                        if(ingredient.getRight() == stack.getItem()){
                            int maxToRemove = Math.min(ingredient.getLeft(), stack.getCount());
                            if(ingredient.getLeft() == maxToRemove){
                                remainingIngredients.remove(i);
                                if(remainingIngredients.isEmpty())
                                    return true;
                            }else{
                                remainingIngredients.set(i, new Pair<Integer, Item>(ingredient.getLeft() - maxToRemove, ingredient.getRight()));
                            }
                        }
                    }
                }
            }

            return false;
        }

        // consumes the earlier items in the input list first
        public ItemStack craft(List<Entity> inputs, boolean forRealzies){
            List<Pair<Integer, Item>> remainingIngredients = new ArrayList<>(ingredients.get());
            for(Entity ent : inputs){
                if(ent instanceof ItemEntity itemEnt){
                    ItemStack stack = itemEnt.getStack();
                    if(stack.isEmpty())
                        continue;
                    for(int i = 0; i < remainingIngredients.size(); i++){
                        Pair<Integer, Item> ingredient = remainingIngredients.get(i);
                        if(ingredient.getRight() == stack.getItem()){
                            int maxToRemove = Math.min(ingredient.getLeft(), stack.getCount());
                            if(ingredient.getLeft() == maxToRemove){
                                remainingIngredients.remove(i);
                            }else{
                                remainingIngredients.set(i, new Pair<Integer, Item>(ingredient.getLeft() - maxToRemove, ingredient.getRight()));
                            }
                            if(forRealzies){
                                stack.decrement(maxToRemove);
                                if(stack.isEmpty())
                                    itemEnt.discard();
                            }
                            if(remainingIngredients.isEmpty())
                                return output.get().copy();
                        }
                    }
                }
            }
            return output.get().copy();
        }

        public ItemStack getOutput(){
            return output.get().copy();
        }

        public int getPriority(){
            return priority;
        }

        public int getMediaCost(){
            return cost;
        }
    }
}
