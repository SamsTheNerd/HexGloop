package com.samsthenerd.hexgloop.mixins.recipes;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.item.ColorizerItem;
import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public class MixinBrewCastingPotion {
    
    @Inject(method = "craft(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void brewCastingPotion(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if(!input.isEmpty() && input.getNbt().getString("Potion").equals("minecraft:thick")){
            if(ingredient.getItem() == HexItems.AMETHYST_DUST){
                cir.setReturnValue(HexGloopItems.CASTING_POTION_ITEM.get().getDefaultStack());
            }
            if(ingredient.getItem() instanceof ColorizerItem){
                // get rekt only i can use the soulglimmer potion (sorry, idk how to make it work for everyone)
                UUID uuid = UUID.fromString("6f07899c-2b26-4221-8033-1f53f7a0e111");
                FrozenColorizer thisFrozen = new FrozenColorizer(ingredient, uuid);
                ItemStack colorizedPotion = HexGloopItems.CASTING_POTION_ITEM.get().withColorizer(HexGloopItems.CASTING_POTION_ITEM.get().getDefaultStack(), thisFrozen);
                cir.setReturnValue(colorizedPotion);
            }
        }
    }

    @Inject(method = "hasRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void giveCastingRecipe(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir){
        if(!input.isEmpty()){
            NbtCompound tag = input.getNbt();
            if(tag == null){
                return;
            }
            if(!tag.contains("Potion") || !tag.getString("Potion").equals("minecraft:thick")){
                return;
            }
            if(ingredient.getItem() == HexItems.AMETHYST_DUST || ingredient.getItem() instanceof ColorizerItem){
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method="isPotionRecipeIngredient(Lnet/minecraft/item/ItemStack;)Z", at=@At("HEAD"), cancellable=true)
    private static void isPotionRecipeIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(stack.getItem() instanceof ColorizerItem || stack.getItem() == HexItems.AMETHYST_DUST){
            cir.setReturnValue(true);
        }
    }
}
