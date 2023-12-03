package com.samsthenerd.hexgloop.recipes;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.IMindTargetItem;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.common.recipe.BrainsweepRecipe;
import at.petrak.hexcasting.common.recipe.HexRecipeStuffRegistry;
import at.petrak.hexcasting.common.recipe.ingredient.VillagerIngredient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ItemFlayingRecipe implements Recipe<Inventory> {

    public static class Type implements RecipeType<ItemFlayingRecipe> {
		private Type() {}
		public static final Type INSTANCE = new Type();
		public static final String ID = "hexgloop:item_flaying";
	}

    private Identifier id;
    public final Pair<Ingredient, Integer> ingredient; // ingredient and count required
    public final VillagerIngredient villagerIn;
    public final Item result;
    public final boolean preserveNbt;
    public final boolean addVillagerNbt;
    public final NbtCompound addedNbt;
    public final int resultCount;

    public ItemFlayingRecipe(Identifier id, Pair<Ingredient, Integer> ingredient, VillagerIngredient villagerIn, Item result, boolean preserveNbt, boolean addVillagerNbt, NbtCompound addedNbt, int resultCount) {
        this.id = id;
        this.ingredient = ingredient;
        this.villagerIn = villagerIn;
        this.result = result;
        this.preserveNbt = preserveNbt;
        this.addVillagerNbt = addVillagerNbt;
        this.addedNbt = addedNbt;
        this.resultCount = resultCount;
    }
    

    public boolean matches(ItemStack inStack, VillagerEntity villagerIn) {
        return ingredient.getLeft().test(inStack) && inStack.getCount() >= ingredient.getRight() && this.villagerIn.test(villagerIn);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return HexRecipeStuffRegistry.BRAINSWEEP;
    }

    // in order to get this to be a "Recipe" we need to do a lot of bending-over-backwards
    // to get the implementation to be satisfied even though we never use it
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

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY.copy();
    }

    private static final Map<Item, IMindTargetItem> ITEM_FLAYING_HANDLERS = new HashMap<>();
    private static final Map<Item, IMindTargetItem> itemFlayingCache = new HashMap<>();

    // TODO: modify this to get base hex block recipes too
    @Nullable
    public static IMindTargetItem getItemTarget(ItemStack stack, VillagerEntity villager, RecipeManager recman){
        // get it from an interface
        if(stack.getItem() instanceof IMindTargetItem target){
            return target;
        }
        // get it from an external handler
        if(ITEM_FLAYING_HANDLERS.containsKey(stack.getItem())){
            return ITEM_FLAYING_HANDLERS.get(stack.getItem());
        }
        // check the cache before rechecking the recipes
        if(itemFlayingCache.containsKey(stack.getItem()))
            return itemFlayingCache.get(stack.getItem());
        // get it from a recipe
        for(ItemFlayingRecipe recipe : recman.listAllOfType(Type.INSTANCE)){
            if(recipe.matches(stack, villager)){
                IMindTargetItem target =  new MindRecipeItem(recipe);
                itemFlayingCache.put(stack.getItem(), target);
                return target;
            }
        }
        // try to find block
        if(stack.getItem() instanceof BlockItem blockItem){
            BlockState inState = blockItem.getBlock().getDefaultState();
            for(BrainsweepRecipe recipe : recman.listAllOfType(HexRecipeStuffRegistry.BRAINSWEEP_TYPE)){
                if(recipe.matches(inState, villager) && recipe.result().getBlock() != blockItem.getBlock()){
                    IMindTargetItem target =  new MindRecipeBlock(recipe);
                    itemFlayingCache.put(stack.getItem(), target);
                    return target;
                }
            }
        }
        itemFlayingCache.put(stack.getItem(), null);
        return null;
    }

    public static boolean addItemFlayingHandler(Item item, IMindTargetItem handler){
        if(ITEM_FLAYING_HANDLERS.containsKey(item))
            return false;
        ITEM_FLAYING_HANDLERS.put(item, handler);
        return true;
    }

    // so that we can reload it with data reload
    public static void reloadRecipes(){
        itemFlayingCache.clear();
    }

    public static class Serializer implements RecipeSerializer<ItemFlayingRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull ItemFlayingRecipe read(Identifier recipeID, JsonObject json) {
            // get villager ingredient
            VillagerIngredient villagerIn = VillagerIngredient.deserialize(JsonHelper.getObject(json, "villagerIn"));
            // get item ingredient
            Ingredient ingredient = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
            int inCount = JsonHelper.getInt(json, "inCount", 1);
            // get result
            Item result = JsonHelper.getItem(json, "result");
            int resultCount = JsonHelper.getInt(json, "resultCount", 1);
            // nbt stuff
            boolean preserveNbt = JsonHelper.getBoolean(json, "preserveNbt", true);
            boolean addVillagerNbt = JsonHelper.getBoolean(json, "addVillagerNbt", false);
            NbtCompound addedNbt = new NbtCompound();
            if(json.has("addedNbt")){
                try {
                    addedNbt = StringNbtReader.parse(JsonHelper.getString(json, "addedNbt"));
                } catch(Exception e){
                    HexGloop.LOGGER.error("Error parsing addedNbt for brainsweep recipe " + recipeID, e);
                }
            }
            return new ItemFlayingRecipe(recipeID, new Pair<Ingredient, Integer>(ingredient, inCount), villagerIn, result, preserveNbt, addVillagerNbt, addedNbt, resultCount);
        }

        @Override
        public void write(PacketByteBuf buf, ItemFlayingRecipe recipe) {
            // villager ingredient
            recipe.villagerIn.write(buf);
            // item ingredient
            recipe.ingredient.getLeft().write(buf);
            buf.writeVarInt(recipe.ingredient.getRight());
            // result
            buf.writeIdentifier(Registry.ITEM.getId(recipe.result));
            buf.writeVarInt(recipe.resultCount);
            // nbt stuff
            buf.writeBoolean(recipe.preserveNbt);
            buf.writeBoolean(recipe.addVillagerNbt);
            buf.writeNbt(recipe.addedNbt);
        }

        @Override
        public @NotNull ItemFlayingRecipe read(Identifier recipeID, PacketByteBuf buf) {
            // villager ingredient
            VillagerIngredient villagerIn = VillagerIngredient.read(buf);
            // item ingredient
            Ingredient ingredient = Ingredient.fromPacket(buf);
            int inCount = buf.readVarInt();
            // result
            Item result = Registry.ITEM.get(buf.readIdentifier());
            int resultCount = buf.readVarInt();
            // nbt stuff
            boolean preserveNbt = buf.readBoolean();
            boolean addVillagerNbt = buf.readBoolean();
            NbtCompound addedNbt = buf.readNbt();
            return new ItemFlayingRecipe(recipeID, new Pair<Ingredient, Integer>(ingredient, inCount), villagerIn, result, preserveNbt, addVillagerNbt, addedNbt, resultCount);
        }
    }

    // wrap all the recipe logic in this class so we can just have a single interface for dealing with flaying into items
    public static class MindRecipeItem implements IMindTargetItem{
        private final ItemFlayingRecipe recipe;

        public MindRecipeItem(ItemFlayingRecipe recipe){
            this.recipe = recipe;
        }

        public ItemStack absorbVillagerMind(VillagerEntity sacrifice, ItemStack stack, CastingContext ctx){
            ItemStack original = stack.copy();
            stack.decrement(recipe.ingredient.getRight());

            ItemStack result = new ItemStack(recipe.result, recipe.resultCount);
            NbtCompound nbt = new NbtCompound();
            if(recipe.preserveNbt && original.hasNbt()){
                nbt = original.getOrCreateNbt().copy();
            }
            // add in any new nbt needed
            nbt.copyFrom(recipe.addedNbt);
            if(recipe.addVillagerNbt){
                NbtCompound villagerNbt = new NbtCompound();
                sacrifice.saveSelfNbt(villagerNbt);
                nbt.put(IMindTargetItem.STORED_MIND_TAG, villagerNbt);
            }
            result.setNbt(nbt);
            return result;
        }
    
        public boolean canAcceptMind(VillagerEntity sacrifice, ItemStack stack, CastingContext ctx){
            return recipe.matches(stack, sacrifice);
        }

    }

    public static class MindRecipeBlock implements IMindTargetItem{
        private final BrainsweepRecipe recipe;

        public MindRecipeBlock(BrainsweepRecipe recipe){
            this.recipe = recipe;
        }

        public ItemStack absorbVillagerMind(VillagerEntity sacrifice, ItemStack stack, CastingContext ctx){
            ItemStack original = stack.copy();
            stack.decrement(1);

            ItemStack result = new ItemStack(recipe.result().getBlock().asItem(), 1);
            return result;
        }
    
        public boolean canAcceptMind(VillagerEntity sacrifice, ItemStack stack, CastingContext ctx){
            if(!(stack.getItem() instanceof BlockItem blockItem)) return false;
            BlockState inState = blockItem.getBlock().getDefaultState();
            return recipe.matches(inState, sacrifice) && recipe.result().getBlock() != blockItem.getBlock();
        }

    }
}