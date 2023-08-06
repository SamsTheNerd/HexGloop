// package com.samsthenerd.hexgloop.recipes;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

// import com.samsthenerd.hexgloop.items.HexGloopItems;

// import at.petrak.hexcasting.common.lib.HexItems;
// import net.minecraft.inventory.CraftingInventory;
// import net.minecraft.item.Item;
// import net.minecraft.item.ItemStack;
// import net.minecraft.recipe.SpecialCraftingRecipe;
// import net.minecraft.recipe.SpecialRecipeSerializer;
// import net.minecraft.tag.TagKey;
// import net.minecraft.util.Identifier;
// import net.minecraft.util.registry.Registry;
// import net.minecraft.world.World;

// public class GloopDyeItemRecipe extends SpecialCraftingRecipe{
//     public static final SpecialRecipeSerializer<GloopDyeItemRecipe> SERIALIZER =
//         new SpecialRecipeSerializer<GloopDyeItemRecipe>(GloopDyeItemRecipe::new);

//     public static final TagKey<Item> DYE_TAG = TagKey.of(Registry.ITEM_KEY, new Identifier("c", "dyes"));
    
//     public GloopDyeItemRecipe(Identifier identifier) {
//         super(identifier);
//     }

//     private int coordToIndex(int startX, int startY, int x, int y){
//         return (y - startY) * 3 + (x - startX);
//     }

//     private List<Integer> GLOOP_INDECES = Arrays.asList(0, 2, 6, 8);
//     private List<Integer> DYE_INDECES = Arrays.asList(1, 3, 5, 7);

//     @Override
//     public boolean matches(CraftingInventory craftingInventory, World world) {
//         int startX = -1;
//         int startY = -1;
//         List<Integer> filledGloopSlots = new ArrayList<Integer>();
//         List<Integer> filledDyeSlots = new ArrayList<Integer>();
//         boolean hasAmethyst = false;
//         for (int i = 0; i < craftingInventory.size(); ++i) {
//             ItemStack stack = craftingInventory.getStack(i);
//             if (stack.isEmpty()) continue;
//             int x = i % craftingInventory.getWidth();
//             int y = i / craftingInventory.getWidth();
//             if (stack.getItem() == HexGloopItems.GLOOP_ITEM) {
//                 if(startX == -1){
//                     startX = x;
//                     startY = y;
//                     filledGloopSlots.add(0);
//                 } else {
//                     if(!GLOOP_INDECES.contains(coordToIndex(startX, startY, x, y))){
//                         return false;
//                     }
//                     filledGloopSlots.add(coordToIndex(startX, startY, x, y));

//                 }
//             } else if(stack.isIn(DYE_TAG)){
//                 if(startX == -1) return false;
//                 if(!DYE_INDECES.contains(coordToIndex(startX, startY, x, y))){
//                     return false;
//                 }
//                 filledDyeSlots.add(coordToIndex(startX, startY, x, y));
//             } else if(stack.getItem() == HexItems.AMETHYST_DUST){
//                 if(startX == -1 || coordToIndex(startX, startY, x, y) != 4) return false;
//                 hasAmethyst = true;
//             }
//         }
//         return hasAmethyst && filledGloopSlots.size() == 4 && filledDyeSlots.size() == 4;
//     }
// }
