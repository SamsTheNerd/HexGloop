package com.samsthenerd.hexgloop.items;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.casting.mirror.IPlayerPTUContext;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
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
        // HexGloop.logPrint("~entered use~");
        SimplePTUContext<TypedActionResult<ItemStack>> useContext = new SimplePTUContext<>(world, user, hand, this, (ctx)->{
            if(user.getItemCooldownManager().isCoolingDown(ctx.storedItemRef.getItem())){
                // hopefully it no-ops fine ?
                return new Pair<>(TypedActionResult.pass(ctx.originalHandStackRef), ctx.storedItemRef);
            }
            TypedActionResult<ItemStack> result = ctx.storedItemRef.use(world, user, hand);
            ItemStack newStackToStore = result.getValue();
            if(newStackToStore != ctx.storedItemRef) newStackToStore = newStackToStore.copy(); // copy incase it's somehow getting cleared elsewhere or something ?
            ctx.storedItemRef = newStackToStore;
            return new Pair<>(result, newStackToStore);
        });
        TypedActionResult<ItemStack> result = useContext.call();
        return useContext.didSucceed ? new TypedActionResult<ItemStack>(result.getResult(), user.getStackInHand(hand)) : super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // HexGloop.logPrint("~entered use on block~");
        // lol, a bit confusing with context vs ctx, oops
        SimplePTUContext<ActionResult> useContext = new SimplePTUContext<>(context.getWorld(), context.getPlayer(), context.getHand(), this, (ctx)->{
            // have to recreate it since the getter is protected on ItemUsageContext
            BlockHitResult hitResult = new BlockHitResult(context.getHitPos(), context.getSide(), context.getBlockPos(), context.hitsInsideBlock());
            // need a new one so it uses the storedItem stack
            ItemUsageContext newContext = new ItemUsageContext(context.getWorld(), context.getPlayer(), context.getHand(), ctx.storedItemRef, hitResult);
            ActionResult result = ctx.storedItemRef.useOnBlock(newContext);
            return new Pair<>(result, PTU_YIELD_CHECK_MARKER);
        });
        ActionResult result = useContext.call();
        return useContext.didSucceed ? result : super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand){
        // HexGloop.logPrint("~entered use on entity~");
        SimplePTUContext<ActionResult> useContext = new SimplePTUContext<>(user.getWorld(), user, hand, this, (ctx)->{
            ActionResult result = ctx.storedItemRef.useOnEntity(user, entity, hand);
            return new Pair<>(result, PTU_YIELD_CHECK_MARKER);
        });
        ActionResult result = useContext.call();
        return useContext.didSucceed ? result : super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        // HexGloop.logPrint("~entered finish using~");
        Hand hand = user.getActiveHand();
        SimplePTUContext<ItemStack> useContext = new SimplePTUContext<>(world, user, hand, this, (ctx)->{
            ItemStack modifiedStoredStack = ctx.storedItemRef.getItem().finishUsing(ctx.storedItemRef, world, user);
            return new Pair<>(modifiedStoredStack, modifiedStoredStack);
        });
        useContext.call();
        return useContext.didSucceed ? user.getStackInHand(hand) : super.finishUsing(stack, world, user);
    }

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // HexGloop.logPrint("~entered stopped using");
        Hand hand = user.getActiveHand();
        SimplePTUContext<Void> useContext = new SimplePTUContext<>(world, user, hand, this, (ctx)->{
            ctx.storedItemRef.getItem().onStoppedUsing(ctx.storedItemRef, world, user, remainingUseTicks);
            return new Pair<>(null, PTU_YIELD_CHECK_MARKER);
        });
        useContext.call();
	}

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        // HexGloop.logPrint("~entered usage tick");
        Hand hand = user.getActiveHand();
        SimplePTUContext<Void> useContext = new SimplePTUContext<>(world, user, hand, this, (ctx)->{
            ctx.storedItemRef.getItem().usageTick(world, user, ctx.storedItemRef, remainingUseTicks);
            return new Pair<>(null, PTU_YIELD_CHECK_MARKER);
        });
        useContext.call();
	}

    // stuff below here doesn't require swapping hand stacks

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
            storedItem.getItem().postHit(storedItem, target, attacker);
            // storedItem.damage(2, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (!world.isClient && state.getHardness(world, pos) != 0.0f) {
            ItemStack storedItem = getStoredItem(stack, miner, world, miner.getActiveHand());
            if(storedItem != null){
                storedItem.getItem().postMine(storedItem, world, state, pos, miner);
                // storedItem.damage(1, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
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
    public boolean isUsedOnRelease(ItemStack stack) {
        ItemStack storedItem = getStoredItemCopy(stack);
        if(storedItem != null && !storedItem.isEmpty() && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){
            return storedItem.getItem().isUsedOnRelease(storedItem);
        }
        return super.isUsedOnRelease(stack);
    }

    // hopefully works fine with deferred registry,,
    // anyways, pass this as a result of the use function to indicate that the stored item should be retrieved from wherever the 
    // pt item got off to. *usually* this will just be the hand,, but that's not guaranteed
    public static final ItemStack PTU_YIELD_CHECK_MARKER = new ItemStack(Items.BRICK);
    static {
        PTU_YIELD_CHECK_MARKER.getOrCreateNbt().putBoolean("ptu_yield_check_marker", true);
    }

    public static boolean isMarker(ItemStack stack){
        return stack.isItemEqual(PTU_YIELD_CHECK_MARKER) && stack.getNbt() != null && stack.getNbt().getBoolean("ptu_yield_check_marker");
    }

    // an abstracting class for the pass through use functions.
    // perhaps unnecessarily complicated but should be worth it if I ever want to add/change any behaviors - which I probably will be doing soon,,
    // ok,, i forgot how lambdas work and so the args stuff probably shouldn't be used in most cases, but i'm leaving it incase it is
    public static class PassThroughUseContext<A, R> {
        // just kinda general stuff for function calls - should maybe have these be getters but nah 
        public World world;
        public LivingEntity user;
        public Hand hand;
        public ItemAbstractPassThrough passItem;
        public A args;
        public ItemStack originalHandStackRef;
        public ItemStack storedItemRef; // mostly for our functions to use
        public boolean didSucceed = false;
        public int ptSlot = -1; // since we have stuff that can swap the hotbar slot, this will make sure we put it back in the right spot

        // let A be vague since different use calls take very different things
        // returns a pair with the 
        public BiFunction<PassThroughUseContext<A,R>, A, Pair<R, ItemStack>> useWrapper;

        @Nullable // if it's null then probably just call the super
        public R call(){
            ItemStack handStack = user.getStackInHand(hand); // important since we'll need to restore it
            originalHandStackRef = handStack; // just so we have it
            ItemStack storedItem = passItem.getStoredItem(handStack, user, world, hand);
            String sideString = world.isClient ? "[client] " : "[server] ";
            if(storedItem != null) storedItem = storedItem.copy();
            if(storedItem != null && !(storedItem.getItem() instanceof ItemAbstractPassThrough)){ // no loops!
                if(user instanceof ServerPlayerEntity sPlayer){
                    ptSlot = hand == Hand.MAIN_HAND ? sPlayer.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
                    ((IPlayerPTUContext)(Object)sPlayer).setPTUContext(this);
                }
                // HexGloop.logPrint(sideString + "swapping stored item into hand: " + storedItem.toString());
                user.setStackInHand(hand, storedItem); // hopefully works fine sided ?
                storedItemRef = storedItem; // so that it can be accessed neatly from the wrapper
                Pair<R, ItemStack> result = useWrapper.apply(this, args);
                storedItem = result.getRight();
                // HexGloop.logPrint(sideString + "result is: " + storedItem.toString());
                if(isMarker(storedItem)){
                    storedItem = user.getStackInHand(hand); // for non-players
                    // HexGloop.logPrint(sideString + "determined it is a marker, found item in hand: " + storedItem.toString());
                    if(user instanceof ServerPlayerEntity sPlayer){
                        PlayerInventory inv = sPlayer.getInventory();
                        storedItem = inv.getStack(ptSlot);
                        // HexGloop.logPrint(sideString + "used by player, found item in ptSlot[" + ptSlot + "]: " + storedItem.toString());
                    }
                }
                // HexGloop.logPrint(sideString + "using stored item: " + storedItem.toString());
                handStack = passItem.setStoredItem(handStack, user, world, hand, storedItem);
                // put the hand stack into wherever it got off to, or if it's not a player just assume it's still in the hand
                // HexGloop.logPrint(sideString + "got handstack back: " + handStack.toString());
                if(user instanceof ServerPlayerEntity sPlayer){
                    PlayerInventory inv = sPlayer.getInventory();
                    // HexGloop.logPrint(sideString + "putting handstack back into inventory at slot " + ptSlot + ": " + handStack.toString());
                    inv.setStack(ptSlot, handStack);
                    ((IPlayerPTUContext)(Object)sPlayer).clearPTUContext();
                } else {
                    // HexGloop.logPrint(sideString + "putting handstack back into hand: " + handStack.toString());
                    user.setStackInHand(hand, handStack);
                }
                didSucceed = true;
                return result.getLeft();
            }
            return null;
        }

        public PassThroughUseContext(World world, LivingEntity user, Hand hand, ItemAbstractPassThrough passItem, BiFunction<PassThroughUseContext<A,R>, A, Pair<R, ItemStack>> useWrapper, A args){
            this.world = world;
            this.user = user;
            this.hand = hand;
            this.passItem = passItem;
            this.args = args;
            this.useWrapper = useWrapper;
        }
    }

    public static class SimplePTUContext<R> extends PassThroughUseContext<Void, R> {
        public SimplePTUContext(World world, LivingEntity user, Hand hand, ItemAbstractPassThrough passItem, Function<PassThroughUseContext<Void, R>, Pair<R, ItemStack>> useWrapper){
            super(world, user, hand, passItem, (ctx, args) -> useWrapper.apply(ctx), null);
        }
    }
}
