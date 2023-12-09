package com.samsthenerd.hexgloop.patchouli;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.recipes.DataGloopingRecipe;

import at.petrak.hexcasting.common.items.magic.ItemMediaHolder;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class GloopcipeProcessor implements IComponentProcessor {
    private DataGloopingRecipe recipe;

    @Override
    public void setup(IVariableProvider vars) {
        var id = new Identifier(vars.get("recipe").asString());

        var recman = MinecraftClient.getInstance().world.getRecipeManager();
        var brainsweepings = recman.listAllOfType(DataGloopingRecipe.Type.INSTANCE);
        for (var poisonApples : brainsweepings) {
            if (poisonApples.getId().equals(id)) {
                this.recipe = poisonApples;
                break;
            }
        }
    }

    @Override
    public IVariable process(String key) {
        if (this.recipe == null) {
            return null;
        }

        // inputX
        // 012345
        if(key.length() == 6 && key.substring(0, 5).equals("input")){
            var inputStacks = this.recipe.ingredients;
            int index = Integer.parseInt(key.substring(5, 6)) - 1;
            if(index < inputStacks.size()){
                List<ItemStack> stacks = new ArrayList<ItemStack>();
                for(ItemStack stack : inputStacks.get(index).getLeft().getMatchingStacks()){
                    ItemStack pageStack = stack.copy();
                    pageStack.setCount(inputStacks.get(index).getRight());
                    stacks.add(pageStack);
                }
                return IVariable.from(stacks.toArray(new ItemStack[0]));
            } else {
                return IVariable.from(new ItemStack(Items.AIR));
            }
        }

        switch (key) {
            case "header" -> {
                return IVariable.from(this.recipe.getOutput().getName());
            }
            case "result" -> {
                return IVariable.from(this.recipe.getOutput());
            }
            case "dustIcon" -> {
                return IVariable.from(new ItemStack(HexItems.AMETHYST_DUST));
            }
            case "mediaCost" -> {
                return IVariable.from(Text.translatable("hexgloop.hexdoc.gloopcipe_media_cost", this.recipe.getMediaCost()/10000)
                    .setStyle(Style.EMPTY.withColor(ItemMediaHolder.HEX_COLOR)));
            }
            default -> {
                return null;
            }
        }
    }
}

