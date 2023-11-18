package com.samsthenerd.hexgloop.items;

import java.util.UUID;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
    public static final UUID zeroUuid = new UUID(0,0);

    public ItemCastingPotion(Settings settings){
        super(settings);
    }

    public int cooldown(){
        return 0;
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

    // shibva's idea for syncing pigment to player
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(world.isClient) return;
        NbtCompound nbt = stack.getNbt();
        if(nbt != null && nbt.contains(TAG_COLORIZER, NbtElement.COMPOUND_TYPE)){
            NbtCompound colorizerNbt = nbt.getCompound(TAG_COLORIZER);
            if(colorizerNbt.contains(FrozenColorizer.TAG_OWNER, NbtElement.INT_ARRAY_TYPE)){
                UUID uuid = colorizerNbt.getUuid(FrozenColorizer.TAG_OWNER);
                if(uuid.equals(zeroUuid)){
                    colorizerNbt.putUuid(FrozenColorizer.TAG_OWNER, entity.getUuid());
                    nbt.put(TAG_COLORIZER, colorizerNbt);
                    stack.setNbt(nbt);
                }
            }
        }
    }

    // make it work like a potion
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(!hasHex(user.getStackInHand(hand))){
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        ItemStack oldStack = stack.copy();
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }

        FrozenColorizer storedPigment = IXplatAbstractions.INSTANCE.getColorizer(playerEntity);
        FrozenColorizer potionPigment = getColorizer(stack);
        if(potionPigment == null) potionPigment = storedPigment;
        IXplatAbstractions.INSTANCE.setColorizer(playerEntity, potionPigment);
        super.use(world, playerEntity, user.getActiveHand());
        FrozenColorizer currentPigment = IXplatAbstractions.INSTANCE.getColorizer(playerEntity);
        // only set it back if it didn't change from the potion, so we don't break hexbound's recall pigment
        if(currentPigment.equals(potionPigment) || currentPigment == potionPigment) // idk for sure if .equals is implemented properly and idc enough to check
            IXplatAbstractions.INSTANCE.setColorizer(playerEntity, storedPigment);

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
                // need to make sure it still has media if it was in a stack
                setMedia(stack, getMaxMedia(oldStack));
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
