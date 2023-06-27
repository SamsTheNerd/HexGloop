package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class ItemCastingPotion extends ItemPackagedHex{
    public static String TAG_COLORIZER = "colorizer";

    public ItemCastingPotion(Settings settings){
        super(settings);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if(this.isIn(group)){
            stacks.add(getDefaultStack());
            // UUID uuid = UUID.randomUUID(); // maybe put mine in ? just for funzies
            // UUID uuid = UUID.fromString("6f07899c-2b26-4221-8033-1f53f7a0e111");
        //     FrozenColorizer transColor = new FrozenColorizer(HexItems.PRIDE_COLORIZERS.get(ItemPrideColorizer.Type.TRANSGENDER).getDefaultStack(), uuid);
        //     stacks.add(withColorizer(getDefaultStack(), transColor));
        //     FrozenColorizer greenColor = new FrozenColorizer(HexItems.DYE_COLORIZERS.get(DyeColor.GREEN).getDefaultStack(), uuid);
        //     stacks.add(withColorizer(getDefaultStack(), greenColor));
        //     FrozenColorizer pinkColor = new FrozenColorizer(HexItems.DYE_COLORIZERS.get(DyeColor.PINK).getDefaultStack(), uuid);
        //     stacks.add(withColorizer(getDefaultStack(), pinkColor));
        }
    }

    @Override
    public boolean canDrawMediaFromInventory(ItemStack stack) {
        return false;
    }

    @Override
    public boolean breakAfterDepletion() {
        return false;
    }

    public ItemStack withColorizer(ItemStack existingStack, FrozenColorizer colorizer){
        ItemStack newStack = existingStack.copy();
        NbtCompound tag = newStack.getOrCreateNbt();
        tag.put(TAG_COLORIZER, colorizer.serializeToNBT());
        return newStack;
    }

    public FrozenColorizer getColorizer(ItemStack stack){
        NbtCompound tag = stack.getNbt();
        if(tag == null || tag.isEmpty()){
            return null;
        }
        NbtCompound colorizerTag = tag.getCompound(TAG_COLORIZER);
        if(colorizerTag == null || colorizerTag.isEmpty()){
            return null;
        }
        return FrozenColorizer.fromNBT(colorizerTag);
    }

    // make it work like a potion
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }
        super.use(world, playerEntity, user.getActiveHand());
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        if (playerEntity == null || !playerEntity.getAbilities().creativeMode) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        if(hasHex(stack))
            return 32;
        return 0;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        if(hasHex(stack))
            return UseAction.DRINK;
        return UseAction.NONE;
    }
}
