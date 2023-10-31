package com.samsthenerd.hexgloop.recipes;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.samsthenerd.hexgloop.recipes.GloopingRecipes.GloopingRecipe;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.world.World;

// probably only gonna be used for item only recipes
public class DataGloopingRecipe implements Recipe<Inventory>, GloopingRecipe{
    public static class Type implements RecipeType<DataGloopingRecipe> {
		private Type() {}
		public static final Type INSTANCE = new Type();
		public static final String ID = "hexgloop:data_glooping";
	}

    private final List<Pair<Ingredient, Integer>> ingredients;
    private final ItemStack result;
    private final Identifier id;
    private final int mediaCost;
    private final int priority;
 
	public DataGloopingRecipe(Identifier id, List<Pair<Ingredient, Integer>> ingredients, ItemStack result, int mediaCost, int priority) {
		this.id = id;
		this.ingredients = ingredients;
		this.result = result;
        this.mediaCost = mediaCost;
        this.priority = priority;
	}

    @Override
    public String toString(){
        String ingredientString = "";
        for(Pair<Ingredient, Integer> ingredient : ingredients){
            ingredientString += "\n\t\t -x" + ingredient.getRight();
            for(Ingredient.Entry entry : ingredient.getLeft().entries){
                if(entry instanceof Ingredient.StackEntry sEntry){
                    ingredientString += "\n\t\t\t" + sEntry.stack;
                }
                if(entry instanceof Ingredient.TagEntry tEntry){
                    ingredientString += "\n\t\t\t" + tEntry.tag;
                }
            }
        }
        return "DataGloopingRecipe(" + id + ":" +
        "\n\tresult: " + result +
        "\n\tmediaCost: " + mediaCost +
        "\n\tpriority: " + priority +
        "\n\tingredients: " + ingredientString + 
        ")";
    }

    public boolean matches(List<Entity> inputs) {
        List<Pair<Ingredient, Integer>> remainingIngredients = new ArrayList<>(ingredients);
        for(Entity ent : inputs){
            if(ent instanceof ItemEntity itemEnt){
                ItemStack stack = itemEnt.getStack();
                if(stack.isEmpty())
                    continue;
                for(int i = 0; i < remainingIngredients.size(); i++){
                    Pair<Ingredient, Integer> ingredient = remainingIngredients.get(i);
                    if(ingredient.getLeft().test(stack)){
                        int maxToRemove = Math.min(ingredient.getRight(), stack.getCount());
                        if(ingredient.getRight() == maxToRemove){
                            remainingIngredients.remove(i);
                            if(remainingIngredients.isEmpty())
                                return true;
                        }else{
                            remainingIngredients.set(i, new Pair<Ingredient, Integer>(ingredient.getLeft(), ingredient.getRight() - maxToRemove));
                        }
                    }
                }
            }
        }

        return false;
    }

    // consumes the earlier items in the input list first
    public ItemStack craft(List<Entity> inputs, boolean forRealzies){
        List<Pair<Ingredient, Integer>> remainingIngredients = new ArrayList<>(ingredients);
        for(Entity ent : inputs){
            if(ent instanceof ItemEntity itemEnt){
                ItemStack stack = itemEnt.getStack();
                if(stack.isEmpty())
                    continue;
                for(int i = 0; i < remainingIngredients.size(); i++){
                    Pair<Ingredient, Integer> ingredient = remainingIngredients.get(i);
                    if(ingredient.getLeft().test(stack)){
                        int maxToRemove = Math.min(ingredient.getRight(), stack.getCount());
                        if(ingredient.getRight() == maxToRemove){
                            remainingIngredients.remove(i);
                        }else{
                            remainingIngredients.set(i, new Pair<Ingredient, Integer>(ingredient.getLeft(), ingredient.getRight() - maxToRemove));
                        }
                        if(forRealzies){
                            stack.decrement(maxToRemove);
                            if(stack.isEmpty())
                                itemEnt.discard();
                        }
                        if(remainingIngredients.isEmpty())
                            return result.copy();
                    }
                }
            }
        }

        return result.copy();
    }

    public ItemStack getOutput(){
        return result.copy();
    }

    public int getPriority(){
        return priority;
    }

	@Override
	public RecipeType<?> getType() {
		return Type.INSTANCE;
	}

    public Identifier getId() {
        return id;
    }

    // ignore crafting recipe stuff
    @Override
    public boolean matches(Inventory pContainer, World pLevel) {
        return false;
    }

    @Override
    public ItemStack craft(Inventory pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int pWidth, int pHeight) {
        return false;
    }

    public RecipeSerializer<DataGloopingRecipe> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<DataGloopingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull DataGloopingRecipe read(Identifier recipeID, JsonObject json) {
            JsonArray jsonItemsIn = JsonHelper.getArray(json, "ingredients");
            List<Pair<Ingredient, Integer>> ingredients = new ArrayList<>();
            for(JsonElement itemElem : jsonItemsIn){
                if(itemElem.isJsonObject()){
                    JsonObject ingredientOrWithCount = itemElem.getAsJsonObject();
                    Ingredient ingredient;
                    int count;
                    if(ingredientOrWithCount.has("ingredient")){
                        ingredient = Ingredient.fromJson(ingredientOrWithCount.get("ingredient"));
                        count = JsonHelper.getInt(ingredientOrWithCount, "count", 1);
                    } else {
                        ingredient = Ingredient.fromJson(ingredientOrWithCount);
                        count = 1;
                    }
                    ingredients.add(new Pair<>(ingredient, count));
                }
            }
            if(ingredients.isEmpty())
                throw new IllegalStateException("No ingredients for recipe " + recipeID);
            ItemStack result = new ItemStack(JsonHelper.getItem(json, "result"));
            int mediaCost = JsonHelper.getInt(json, "mediaCost", 0);
            int priority = JsonHelper.getInt(json, "priority", 0);
            return new DataGloopingRecipe(recipeID, ingredients, result, mediaCost, priority);
        }

        @Override
        public void write(PacketByteBuf buf, DataGloopingRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for(Pair<Ingredient, Integer> ingredient : recipe.ingredients){
                ingredient.getLeft().write(buf);
                buf.writeVarInt(ingredient.getRight());
            }
            buf.writeItemStack(recipe.result);
            buf.writeVarInt(recipe.mediaCost);
            buf.writeVarInt(recipe.priority);
        }

        @Override
        public @NotNull DataGloopingRecipe read(Identifier recipeID, PacketByteBuf buf) {
            int size = buf.readVarInt();
            List<Pair<Ingredient, Integer>> ingredients = new ArrayList<>();
            for(int i = 0; i < size; i++){
                int count = buf.readVarInt();
                Ingredient ingredient = Ingredient.fromPacket(buf);
                ingredients.add(new Pair<>(ingredient, count));
            }
            ItemStack result = buf.readItemStack();
            int mediaCost = buf.readVarInt();
            int priority = buf.readVarInt();
            return new DataGloopingRecipe(recipeID, ingredients, result, mediaCost, priority);
        }
    }
}
