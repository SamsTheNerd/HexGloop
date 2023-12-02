package com.samsthenerd.hexgloop.patchouli;

import java.util.Objects;

import com.samsthenerd.hexgloop.recipes.ItemFlayingRecipe;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

// yoinked almost entirely from BrainsweepProcessor
public class ItemFlayProcessor implements IComponentProcessor {
    private ItemFlayingRecipe recipe;

    @Override
    public void setup(IVariableProvider vars) {
        var id = new Identifier(vars.get("recipe").asString());

        var recman = MinecraftClient.getInstance().world.getRecipeManager();
        var brainsweepings = recman.listAllOfType(ItemFlayingRecipe.Type.INSTANCE);
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

        switch (key) {
            case "header" -> {
                return IVariable.from(this.recipe.result.getName());
            }
            case "input" -> {
                var inputStacks = this.recipe.ingredient.getLeft().getMatchingStacks();
                return IVariable.from(inputStacks);
            }
            case "result" -> {
                return IVariable.from(new ItemStack(this.recipe.result));
            }

            case "entity" -> {
                var profession = Objects.requireNonNullElse(this.recipe.villagerIn.profession(),
                    new Identifier("toolsmith"));
                var biome = Objects.requireNonNullElse(this.recipe.villagerIn.biome(),
                    new Identifier("plains"));
                var level = this.recipe.villagerIn.minLevel();
                var iHatePatchouli = String.format(
                    "minecraft:villager{VillagerData:{profession:'%s',type:'%s',level:%d}}",
                    profession, biome, level);
                return IVariable.wrap(iHatePatchouli);
            }
            case "entityTooltip" -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                return IVariable.wrapList(this.recipe.villagerIn
                    .getTooltip(mc.options.advancedItemTooltips)
                    .stream()
                    .map(IVariable::from)
                    .toList());
            }
            default -> {
                return null;
            }
        }
    }
}

