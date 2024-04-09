package com.samsthenerd.hexgloop.blockentities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blocks.BlockPedestal;
import com.samsthenerd.hexgloop.blocks.IDynamicFlayTarget;
import com.samsthenerd.hexgloop.blocks.iotic.IIoticProvider;
import com.samsthenerd.hexgloop.items.IMindTargetItem;
import com.samsthenerd.hexgloop.misc.HexGloopTags;
import com.samsthenerd.hexgloop.misc.INoMoving;
import com.samsthenerd.hexgloop.recipes.ItemFlayingRecipe;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

// switching it to sided so that it can be used by modded chutes and whatnot hopefully ?
public class BlockEntityPedestal extends BlockEntity implements Inventory, IReallyHateForgeWhyWouldAnInventoryInterfaceNotBeAnInterfaceThatsWhatAnInterfaceIsFor, IDynamicFlayTarget, IIoticProvider {
    public static final String ITEM_DATA_TAG = "inv_storage";
    public static final String PERSISTENT_UUID_TAG = "persistent_uuid";

    public static final double HEIGHT = 0.75;

    private ItemStack storedItem = ItemStack.EMPTY;
    // item entity on top of pedestal
    private ItemEntity itemEnt = null; 
    private UUID persistentUUID = null;
    
    private boolean isMirror;
    private boolean hasMindStorage;
    private VillagerEntity storedMind = null;

    public BlockEntityPedestal(BlockPos pos, BlockState state) {
        super(HexGloopBEs.PEDESTAL_BE.get(), pos, state);
        isMirror = false;
        hasMindStorage = false;
        if(state.getBlock() instanceof BlockPedestal){
            isMirror = ((BlockPedestal)state.getBlock()).isMirror;
            hasMindStorage = ((BlockPedestal)state.getBlock()).hasMindStorage;
        }
        if(getWorld() != null && !getWorld().isClient()){
            persistentUUID = getNewUUID();
            markDirty();
            makeNewItemEntity();
        }
    }

    private UUID getNewUUID(){
        UUID newUUID;
        boolean isUnique = false;
        do{
            newUUID = UUID.randomUUID();
            if(getWorld() instanceof ServerWorld sWorld){
                isUnique = sWorld.getEntity(newUUID) == null;
            }
        } while(!isUnique);
        return newUUID;
    }

    public UUID getPersistentUUID(){
        return persistentUUID;
    }

    public Direction getNormal(){
        return getCachedState().get(BlockPedestal.FACING);
    }

    private void makeNewItemEntity(){
        if(getWorld() instanceof ServerWorld sWorld){
            if(storedItem == null || storedItem.isEmpty()){
                return;
            }
            if(persistentUUID == null){
                persistentUUID = getNewUUID();
            }
            // first need to deal with old one -- maybe
            if(itemEnt != null){
                itemEnt.discard();
                itemEnt = null;
            }
            Entity maybeItemEnt = sWorld.getEntity(persistentUUID);
            Vec3i norm = getNormal().getVector();
            double heightOffset = HEIGHT - 0.5 + 0.01;
            double xPos = pos.getX() + 0.5 + (heightOffset+0.2f) * norm.getX(); // + 0.125 to put it far enough out
            double yPos = pos.getY() + 0.2 + (heightOffset * norm.getY()) + Math.abs(0.3 * norm.getY()) + (norm.getY() < 0 ? -0.7 : 0); // put it on the 'floor' when it's sideways so it's not floating too high
            double zPos = pos.getZ() + 0.5 + (heightOffset+0.2f) * norm.getZ();
            boolean needsToSpawn = false;
            if(maybeItemEnt instanceof ItemEntity someItemEnt){
                // if(someItemEnt.isRemoved()){
                //     // see if we can restore it -- no clue if this will actually work
                //     ((INoMoving)someItemEnt).callUnsetRemoval();
                // }
                // we have some item entity ? that seems to exist ?
                itemEnt = someItemEnt;
                itemEnt.setStack(storedItem);
            } else {
                needsToSpawn = true;
                itemEnt = new ItemEntity(sWorld, 
                    xPos, yPos, zPos, 
                    storedItem, 0, 0, 0);
            }
            itemEnt.setPos(xPos, yPos, zPos);
            itemEnt.setUuid(persistentUUID);
            itemEnt.setNoGravity(true);
            itemEnt.noClip = true;
            itemEnt.setPickupDelayInfinite();
            itemEnt.setNeverDespawn();
            itemEnt.setInvulnerable(true);
            ((INoMoving)itemEnt).setNoMoving(true);
            if(needsToSpawn){
                sWorld.spawnEntity(itemEnt);
            }
            markDirty();
        }
    }

    public void onRemoved(){
        if(itemEnt != null){
            itemEnt.discard();
        }
        if(world instanceof ServerWorld sWorld){
            Vec3i norm = getNormal().getVector();
            double heightOffset = HEIGHT - 0.5 + 0.01;
            // spawn a normal item entity, just as a drop
            itemEnt = new ItemEntity(sWorld, 
                pos.getX() + 0.5 + heightOffset * norm.getX(), 
                pos.getY() + 0.5 + heightOffset * norm.getY(), 
                pos.getZ() + 0.5 + heightOffset * norm.getZ(), 
                storedItem, 0, 0, 0);
            sWorld.spawnEntity(itemEnt);
            markDirty();
        }
    }

    private Set<PlayerEntity> interactedCheck = new HashSet<>();

    public void interacted(PlayerEntity player){
        interactedCheck.add(player);
    }

    public ActionResult use(PlayerEntity player, Hand hand, BlockHitResult hit){
        // make sure it doesn't get the second hand when it shouldn't
        if(interactedCheck.contains(player)) {
            interactedCheck.remove(player);
            return ActionResult.CONSUME;
        }
        boolean isClient = world.isClient();
        String logLore = "[ " + (isClient ? "client" : "server") + " ] -  " + (hand == Hand.MAIN_HAND ? "main" : "off") + ": ";
        ItemStack heldStack = player.getStackInHand(hand);
        ItemStack otherStack = player.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
        // HexGloop.logPrint("entered use on [ " + (isClient ? "client" : "server") + " ] with heldStack [ " + heldStack + " ] and otherStack [ " + otherStack + " ]");
        if(storedItem==null || storedItem.isEmpty()){ // it's empty, so maybe put something in there
            // HexGloop.logPrint(logLore + "stored item empty");
            if(world.isClient){
                // HexGloop.logPrint(logLore + "returning on client based on heldStack.isEmpty()");
                return heldStack.isEmpty() ? ActionResult.PASS : ActionResult.SUCCESS;
            }
            if(heldStack.isEmpty()){
                // HexGloop.logPrint(logLore + "heldstack empty");
                return ActionResult.PASS;
            } else {
                setStack(0, heldStack.copy());
                heldStack.decrement(heldStack.getCount());
                // HexGloop.logPrint(logLore + "heldstack not empty");
                return ActionResult.SUCCESS;
            }
        }
        // not empty, maybe take something out - or add it
        if(ItemStack.canCombine(storedItem, heldStack)){
            // HexGloop.logPrint(logLore + "can combine with heldStack");
            if(!world.isClient){
                int amtToMove = Math.min(storedItem.getMaxCount() - storedItem.getCount(), heldStack.getCount());
                storedItem.increment(amtToMove);
                heldStack.decrement(amtToMove);
                syncItemWithEntity(true);
            }
            return ActionResult.SUCCESS;
        }
        if(ItemStack.canCombine(storedItem, otherStack)){ // let it be handled by other hand ? 
            // HexGloop.logPrint(logLore + "can combine with otherStack");
            return ActionResult.PASS;
        }
        // can't combine, so pop it and then replace only if main hand ?
        if(!world.isClient){
            ItemStack returnedStack = removeStack(0);
            if(hand == Hand.MAIN_HAND){
                // HexGloop.logPrint(logLore + "putting main hand stack in pedestal");
                setStack(0, heldStack.copy());
                heldStack.decrement(heldStack.getCount());
            }
            if(!returnedStack.isEmpty()){
                // HexGloop.logPrint(logLore + "returning stack to player");
                if(player.getMainHandStack().isEmpty()){
                    player.setStackInHand(Hand.MAIN_HAND, returnedStack);
                } else {
                    player.getInventory().offerOrDrop(returnedStack);
                }
            }
        }
        // HexGloop.logPrint(logLore + "returning success");
        return ActionResult.SUCCESS;
    }

    @Override
    public ADIotaHolder getIotaHolder(World world, BlockPos pos){
        return IXplatAbstractions.INSTANCE.findDataHolder(storedItem);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.storedItem = ItemStack.fromNbt(nbt.getCompound(ITEM_DATA_TAG));
        if(nbt.containsUuid(PERSISTENT_UUID_TAG))
            this.persistentUUID = nbt.getUuid(PERSISTENT_UUID_TAG);
        if(hasMindStorage && nbt.contains("stored_mind")){
            if(EntityType.getEntityFromNbt(nbt.getCompound("stored_mind"), world).orElse(null) instanceof VillagerEntity villager){
                storedMind = villager;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if(storedItem != null){
            nbt.put(ITEM_DATA_TAG, storedItem.writeNbt(new NbtCompound()));
        }
        if(persistentUUID != null){
            nbt.putUuid(PERSISTENT_UUID_TAG, persistentUUID);
        }
        if(storedMind != null && hasMindStorage){
            NbtCompound mindNbt = new NbtCompound();
            storedMind.saveSelfNbt(mindNbt);
            nbt.put("stored_mind", storedMind.writeNbt(mindNbt));
        }
    }

    public void syncItemWithEntity(boolean forceBlock){
        if(world.isClient()) return;
        if(storedItem == null || storedItem.isEmpty()){
            // no stored item so kill our entity
            if(itemEnt != null){
                // actually maybe don't discard it ourselves, it should take care of itself ?
                // itemEnt.discard();
                itemEnt.setStack(storedItem); // empty it, it'll die on tick ?
                itemEnt = null;
                markDirty();
            }
            return;
        } else { // have an item here
            if(itemEnt == null || itemEnt.isRemoved()){ // no entity
                // it's a mess but it just checks if the item entity is either discarded or killed and empty since some spells kill it when it's empty
                if(itemEnt != null && 
                    (itemEnt.getRemovalReason() == RemovalReason.DISCARDED || 
                        (itemEnt.getRemovalReason() == RemovalReason.KILLED && 
                        (itemEnt.getStack() == null || itemEnt.getStack().isEmpty()) 
                    ))
                && !forceBlock){
                    storedItem = ItemStack.EMPTY;
                    markDirty();
                } else {
                    makeNewItemEntity();
                }
            } else { // there is an entity and an item
                if(storedItem != itemEnt.getStack()){
                    if(forceBlock){
                        itemEnt.setStack(storedItem);
                    } else {
                        storedItem = itemEnt.getStack();
                        markDirty();
                    }
                }
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state){
        if(world.isClient()) return;
        interactedCheck.clear();
        List<ItemEntity> hopperableItemEnts = getInputItemEntities();
        hopperableItemEnts.sort((a, b) -> {
            return (int) (pos.getSquaredDistanceFromCenter(a.getPos().x, a.getPos().y, a.getPos().z) - pos.getSquaredDistanceFromCenter(b.getPos().x, b.getPos().y, b.getPos().z));
        });
        // sync entity to here first
        syncItemWithEntity(false);
        boolean wasItemUpdated = false;
        for(ItemEntity iEnt : hopperableItemEnts){
            wasItemUpdated = true;
            ItemStack entStack = iEnt.getStack();
            if(storedItem == null || storedItem.isEmpty()){
                storedItem = entStack.copy();
                entStack.decrement(entStack.getCount());
                break;
            }
            if(ItemStack.canCombine(storedItem, entStack)){
                int amtToMove = Math.min(storedItem.getMaxCount() - storedItem.getCount(), entStack.getCount());
                storedItem.increment(amtToMove);
                entStack.decrement(amtToMove);
                break;
            }
        }
        // force this update if needed
        if(wasItemUpdated) syncItemWithEntity(true);
        // move mind into item if possible
        if(hasMindStorage && !storedItem.isEmpty() && storedMind != null && canHeldItemAcceptMind(storedMind, null)){
            // HexGloop.logPrint("moving mind into item");
            flayHeldItem(storedMind, null);
            storedMind = null;
            world.setBlockState(pos, world.getBlockState(pos).with(BlockPedestal.MINDFUL, false));
            markDirty();
        }
    }

    public List<ItemEntity> getInputItemEntities() {
        // so we had this grab a bit above the pedestal before, but that sounds like a pain rotated, so this should be fine probably ?
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        return world.getEntitiesByClass(ItemEntity.class, box, (ent) -> !(ent.getUuid().equals(persistentUUID)) && EntityPredicates.VALID_ENTITY.test(ent) && !((INoMoving)ent).getNoMoving());
    }
        
    // tries to get a pattern based on the scroll or slate in the pedestal if there is one
    @Nullable
    public HexPattern getPattern(){
        // make sure we're on the server and have a pattern holder
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(storedItem); // so that we can get stuff like ducks or Pi
        if(!(world instanceof ServerWorld sWorld)
        || storedItem.isIn(HexGloopTags.NOT_PATTERN_PEDESTAL_PROVIDER)
        || iotaHolder == null) return null; 
        Iota iota = iotaHolder.readIota(sWorld);
        if(iota instanceof PatternIota pIota){
            return pIota.getPattern();
        }
        return null;
    }

    public Iota getIota(){
        if(!isMirror) return null; // only do embedding stuff on the mirror
        if(!(world instanceof ServerWorld sWorld)) return null;
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(storedItem);
        if(iotaHolder != null){
            return iotaHolder.readIota(sWorld);
        }
        return null;
    }
    
    public int size(){
        return 1;
    }

    public boolean isEmpty(){
        return storedItem.isEmpty();
    }

    public ItemStack getStack(int slot){
        if(slot == 0){
            return storedItem;
        }
        return ItemStack.EMPTY;
    }

    public void setStack(int slot, ItemStack stack){
        if(slot == 0){
            storedItem = stack;
            syncItemWithEntity(true);
            markDirty();
        }
    }

    public ItemStack removeStack(int slot){
        if(slot == 0){
            ItemStack temp = storedItem;
            storedItem = ItemStack.EMPTY;
            syncItemWithEntity(true);
            markDirty();
            return temp;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot, int amount){
        if(slot == 0){
            ItemStack newSplit = storedItem.split(amount);
            syncItemWithEntity(true);
            markDirty();
            return newSplit;
        }
        return ItemStack.EMPTY;
    }

    public boolean canPlayerUse(PlayerEntity player){
        return false; // no gui 
    }

    public void clear(){
        storedItem = ItemStack.EMPTY;
        syncItemWithEntity(true);
        markDirty();
    }

    @Override
    public void markDirty() {
        if (world instanceof ServerWorld sWorld) {
            sWorld.getChunkManager().markForUpdate(pos);
        }
        super.markDirty();
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = new NbtCompound();
        this.writeNbt(tag);
        return tag;
    }

    @Override
    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // flaying stuff:

    // abstracted here so we can call it without casting context and not deal with storing the mind in the pedestal
    public void flayHeldItem(VillagerEntity sacrifice, @Nullable CastingContext ctx){
        // this deals with dynamic stuff and recipes
        World world = getWorld();
        IMindTargetItem mindTarget = ItemFlayingRecipe.getItemTarget(storedItem, sacrifice, world.getRecipeManager());
        if(mindTarget != null){
            ItemStack result = mindTarget.absorbVillagerMind(sacrifice, storedItem, ctx);
            if(result != storedItem){
                if(storedItem.isEmpty()){
                    storedItem = result; // just replace it
                    syncItemWithEntity(true);
                } else {
                    // spawn a new item entity - might be nice to add a bit of velocity to it ?
                    ItemEntity newEnt = new ItemEntity(world, 
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 
                    result, 0, 0, 0);
                    world.spawnEntity(newEnt);
                }
            }
            return;
        }
    }

    public void absorbVillagerMind(VillagerEntity sacrifice, BlockPos flayPos, CastingContext ctx){
    
        if(canHeldItemAcceptMind(sacrifice, ctx)){
            flayHeldItem(sacrifice, ctx);
            return;
        }

        // handle mindstorage
        if(hasMindStorage && storedMind == null){
            VillagerEntity clone = new VillagerEntity(EntityType.VILLAGER, world);
            clone.setVillagerData(sacrifice.getVillagerData());
            clone.setPos(flayPos.getX(), flayPos.getY(), flayPos.getZ());
            storedMind = clone;
            if(storedMind == null){
                world.setBlockState(flayPos, world.getBlockState(flayPos).with(BlockPedestal.MINDFUL, false));
            } else {
                world.setBlockState(flayPos, world.getBlockState(flayPos).with(BlockPedestal.MINDFUL, true));
            }
            markDirty();
        }
    }
    
    public boolean canHeldItemAcceptMind(VillagerEntity sacrifice, CastingContext ctx){
        IMindTargetItem mindTarget = ItemFlayingRecipe.getItemTarget(storedItem, sacrifice, getWorld().getRecipeManager());
        if(mindTarget != null){
            return mindTarget.canAcceptMind(sacrifice, storedItem, ctx);
        }
        return false;
    }

    // return true if it can be accepted
    public boolean canAcceptMind(VillagerEntity sacrifice, BlockPos flayPos, CastingContext ctx){
        
        if(canHeldItemAcceptMind(sacrifice, ctx)){
            return true;
        }

        if(hasMindStorage){
            return storedMind == null;
        }
        
        return false;
    }
}
