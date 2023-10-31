package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopRecipes {
    public static Registrar<RecipeSerializer<?> > recipeSerializers = HexGloop.REGISTRIES.get().get(Registry.RECIPE_SERIALIZER_KEY);
    public static Registrar<RecipeType<?> > recipeTypes = HexGloop.REGISTRIES.get().get(Registry.RECIPE_TYPE_KEY);
    
    public static RegistrySupplier<SpecialRecipeSerializer<SealMultiFocusRecipe>> SEAL_MULTI_FOCUS_RECIPE = 
        register("crafting_seal_multi_focus", SealMultiFocusRecipe.SERIALIZER);
    public static RegistrySupplier <SpecialRecipeSerializer<GloopDyeRecipe>> GLOOP_DYE_RECIPE = 
        register("crafting_gloop_dye", GloopDyeRecipe.SERIALIZER);
    public static RegistrySupplier <SpecialRecipeSerializer<CoveredSpellbookRecipe>> MAKE_DYEBOOK_RECIPE = 
        register("crafting_make_dyebook", CoveredSpellbookRecipe.SERIALIZER);
    public static RegistrySupplier<DataGloopingRecipe.Serializer> DATA_GLOOPING_RECIPE = 
        register("data_glooping", DataGloopingRecipe.Serializer.INSTANCE);
    public static RegistrySupplier<DataGloopingRecipe.Type> DATA_GLOOPING_TYPE = 
        register("data_glooping", DataGloopingRecipe.Type.INSTANCE);
    

    public static void register(){
        GloopingRecipes.init();
    }

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> RegistrySupplier<S> register(String id, S serializer) {
        return recipeSerializers.register(new Identifier(HexGloop.MOD_ID, id), () -> serializer);
    }

    public static <S extends RecipeType<T>, T extends Recipe<?>> RegistrySupplier<S> register(String id, S type) {
        return recipeTypes.register(new Identifier(HexGloop.MOD_ID, id), () -> type);
    }
}
