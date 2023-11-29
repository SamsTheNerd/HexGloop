package com.samsthenerd.hexgloop.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.items.ItemScroll;
import at.petrak.hexcasting.common.lib.HexLootFunctions;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.nbt.NbtCompound;

public class SampleHexesFunc extends ConditionalLootFunction {
    public SampleHexesFunc(LootCondition[] lootItemConditions) {
        super(lootItemConditions);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext ctx) {
        var rand = ctx.getRandom();
        var worldLookup = PatternRegistry.getPerWorldPatterns(ctx.getWorld());

        var keys = worldLookup.keySet().stream().toList();
        var sig = keys.get(rand.nextInt(keys.size()));

        var entry = worldLookup.get(sig);
        var opId = entry.getFirst();
        var startDir = entry.getSecond();
        var tag = new NbtCompound();
        tag.putString(ItemScroll.TAG_OP_ID, opId.toString());
        tag.put(ItemScroll.TAG_PATTERN, HexPattern.fromAngles(sig, startDir).serializeToNBT());

        stack.getOrCreateNbt().copyFrom(tag);

        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return HexLootFunctions.PATTERN_SCROLL;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SampleHexesFunc> {
        @Override
        public void toJson(JsonObject json, SampleHexesFunc value, JsonSerializationContext ctx) {
            super.toJson(json, value, ctx);
        }

        @Override
        public SampleHexesFunc fromJson(JsonObject object, JsonDeserializationContext ctx,
            LootCondition[] conditions) {
            return new SampleHexesFunc(conditions);
        }
    }
}
