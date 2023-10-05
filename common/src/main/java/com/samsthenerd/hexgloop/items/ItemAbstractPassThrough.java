package com.samsthenerd.hexgloop.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// idk probably won't use it for anything else but if i do it'll be nice that it's separate
// makes all the use functions into wrappers that call the use function of some stored item
/**
 * NOTE: a lot of this stuff functioning properly relies on some mixins, so make sure you yoink those too if you want to use this
 * (you here likely being future me, but if you aren't future me you should do that too !)
 * 
 * The mixins generally work by wrapping some original function call and swapping the hand out for the stored item before calling the original 
 * and then storing the result and swapping the original hand stack back in. 
 * There might be *slight* duplication issues here if something gets both the original stored item and the hand copy in the span of the wrapped method
 * 
 * anyways it should all be in mixins/mirroritems, it's mostly for entities and using tools
 */
public abstract class ItemAbstractPassThrough extends Item{
    public ItemAbstractPassThrough(Settings settings){
        super(settings);
    }

    public abstract ItemStack getStoredItem(ItemStack stack, LivingEntity ent, World world, Hand hand);

    // so we can 'put it back' in whatever way makes sense for the item we're using
    // returns the itemstack that should be in the player's hand after the use, just if you need to modify it or something
    public abstract ItemStack setStoredItem(ItemStack stack, LivingEntity ent, World world, Hand hand, ItemStack storedItem);

    // stack is really provided here as a curtesy, it probably shouldn't depend on the stack ?
    public abstract boolean shouldPassTools(ItemStack stack);

    // some methods just don't give us much to work with, support this if it makes sense for the item or just return null if it doesn't
    public abstract ItemStack getStoredItemCopy(ItemStack stack);

    // may be wise to merge some of this code, there's a lot of copy-pasting but with little annoying core changes

    // not super sure what happens with the itemstack in the result here ?
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, user, world, hand);
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
            if(user.getItemCooldownManager().isCoolingDown(storedItem.getItem())){
                return TypedActionResult.pass(handStack);
            }
            user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
            TypedActionResult<ItemStack> result = storedItem.use(world, user, hand);
            // result seems to give the most correct stack ?
            // HexGloop.logPrint("[" + (world.isClient ? "client" : "server") + "]used item, left with:\n\tstoredItem: " + storedItem.toString() + 
            //     "\n\tresult.getValue(): " + result.getValue().toString() +
            //     "\n\tcurrentHand: " + user.getStackInHand(hand).toString());
            ItemStack newStackToStore = result.getValue();
            if(newStackToStore != storedItem) newStackToStore = newStackToStore.copy(); // copy incase it's somehow getting cleared elsewhere or something ?
            handStack = setStoredItem(handStack, user, world, hand, newStackToStore);
            user.setStackInHand(hand, handStack);
            return new TypedActionResult<ItemStack>(result.getResult(), handStack);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack handStack = context.getPlayer().getStackInHand(context.getHand()); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, context.getPlayer(), context.getWorld(), context.getHand());
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            if(context.getPlayer().getItemCooldownManager().isCoolingDown(storedItem.getItem())){
                return ActionResult.PASS;
            }
            context.getPlayer().setStackInHand(context.getHand(), storedItem); // hopefully works fine sided ?
            // have to recreate it since the getter is protected on ItemUsageContext
            BlockHitResult hitResult = new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock());
            // need a new one so it uses the storedItem stack
            ItemUsageContext newContext = new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), storedItem, hitResult);
            ActionResult result = storedItem.useOnBlock(newContext);
            storedItem = context.getPlayer().getStackInHand(context.getHand());
            handStack = setStoredItem(handStack, context.getPlayer(), context.getWorld(), context.getHand(), storedItem);
            context.getPlayer().setStackInHand(context.getHand(), handStack);
            return result;
        }
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand){
        ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, user, user.getWorld(), hand);
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
            user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
            ActionResult result = storedItem.useOnEntity(user, entity, hand);
            handStack = setStoredItem(handStack, user, user.getWorld(), hand, user.getStackInHand(hand));
            user.setStackInHand(hand, handStack);
            return result;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    // stuff for tools
    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        ItemStack storedItem = getStoredItemCopy(stack);
        if(storedItem != null && !(storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough))){
            return storedItem.getItem().getMiningSpeedMultiplier(storedItem, state);
        }
        return super.getMiningSpeedMultiplier(stack, state);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        ItemStack storedItem = getStoredItem(stack, attacker, attacker.getWorld(), attacker.getActiveHand());
        if(storedItem != null){
            storedItem.damage(2, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            ItemStack storedItem = getStoredItem(stack, miner, world, miner.getActiveHand());
            if(storedItem != null){
                storedItem.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        ItemStack storedItem = getStoredItemCopy(miner.getStackInHand(Hand.MAIN_HAND));
        if(storedItem != null && !storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            return storedItem.getItem().canMine(state, world, pos, miner);
        }
        return super.canMine(state, world, pos, miner);
	}

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        Hand hand = user.getActiveHand();
        ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, user, user.getWorld(), hand);
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
            // HexGloop.logPrint("usage ticking passed through item: " + storedItem.toString());
            user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
            storedItem.getItem().usageTick(world, user, storedItem, remainingUseTicks);
            handStack = setStoredItem(handStack, user, user.getWorld(), hand, user.getStackInHand(hand));
            user.setStackInHand(hand, handStack);
        }
	}

    @Override
    public UseAction getUseAction(ItemStack stack) {
        ItemStack storedItem = getStoredItemCopy(stack);
        if(storedItem != null && !storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            return storedItem.getItem().getUseAction(storedItem);
        }
        return super.getUseAction(stack);
	}

    @Override
	public int getMaxUseTime(ItemStack stack) {
        ItemStack storedItem = getStoredItemCopy(stack);
        if(storedItem != null && !storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            return storedItem.getItem().getMaxUseTime(storedItem);
        }
        return super.getMaxUseTime(stack);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        Hand hand = user.getActiveHand();
        ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, user, user.getWorld(), hand);
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
            // HexGloop.logPrint("stopped using passed through item: " + storedItem.toString());
            user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
            storedItem.getItem().onStoppedUsing(storedItem, world, user, remainingUseTicks);
            handStack = setStoredItem(handStack, user, user.getWorld(), hand, user.getStackInHand(hand));
            user.setStackInHand(hand, handStack);
        }
	}

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        ItemStack storedItem = getStoredItemCopy(stack);
        if(storedItem != null && !storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            return storedItem.getItem().isUsedOnRelease(storedItem);
        }
        return super.isUsedOnRelease(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        Hand hand = user.getActiveHand();
        ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
        ItemStack storedItem = getStoredItem(handStack, user, user.getWorld(), hand);
        if(storedItem != null) storedItem = storedItem.copy();
        if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
            // HexGloop.logPrint("finished using passed through item: " + storedItem.toString());
            user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
            storedItem = storedItem.getItem().finishUsing(storedItem, world, user);
            handStack = setStoredItem(handStack, user, user.getWorld(), hand, storedItem);
            user.setStackInHand(hand, handStack);
        }
        return handStack;
    }
}
