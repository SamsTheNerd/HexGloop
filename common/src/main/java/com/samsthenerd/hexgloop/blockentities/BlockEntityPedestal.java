package com.samsthenerd.hexgloop.blockentities;

import java.util.UUID;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.misc.INoMoving;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockEntityPedestal extends BlockEntity implements Inventory {
    public static final String ITEM_DATA_TAG = "inv_storage";
    public static final String PERSISTENT_UUID_TAG = "persistent_uuid";

    private ItemStack storedItem = ItemStack.EMPTY;
    // item entity on top of pedestal
    private ItemEntity itemEnt = null; 
    private UUID persistentUUID = null;

    public BlockEntityPedestal(BlockPos pos, BlockState state) {
        super(HexGloopBEs.PEDESTAL_BE.get(), pos, state);
        if(getWorld() != null && !getWorld().isClient()){
            persistentUUID = getNewUUID();
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

    private void makeNewItemEntity(){
        if(getWorld() instanceof ServerWorld sWorld){
            if(persistentUUID == null){
                persistentUUID = getNewUUID();
            }
            // first need to deal with old one -- maybe
            if(itemEnt != null){
                itemEnt.discard();
            }
            Entity maybeItemEnt = sWorld.getEntity(persistentUUID);
            if(maybeItemEnt != null && !maybeItemEnt.isRemoved()){
                maybeItemEnt.discard();
            }
            itemEnt = new ItemEntity(sWorld, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, storedItem, 0, 0, 0);
            itemEnt.setUuid(persistentUUID);
            itemEnt.setNoGravity(removed);
            itemEnt.noClip = true;
            itemEnt.setPickupDelayInfinite();
            itemEnt.setNeverDespawn();
            itemEnt.setInvulnerable(true);
            ((INoMoving)itemEnt).setNoMoving(true);
            sWorld.spawnEntity(itemEnt);
        }
    }

    

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.storedItem = ItemStack.fromNbt(nbt.getCompound(ITEM_DATA_TAG));
        if(nbt.containsUuid(PERSISTENT_UUID_TAG))
            this.persistentUUID = nbt.getUuid(PERSISTENT_UUID_TAG);
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
    }


    public void syncItemWithEntity(boolean forceBlock){
        if(world.isClient()) return;
        if(storedItem == null || storedItem.isEmpty()){
            if(itemEnt != null){
                itemEnt.discard();
                itemEnt = null;
            }
            return;
        } else { // have an item here
            if(itemEnt == null || itemEnt.isRemoved()){ // no entity
                makeNewItemEntity();
            } else {
                // let's just see how it does here tbh
            }
        }
    }

    public void tick(World world, BlockPos pos, BlockState state){
        if(world.isClient()) return;
        syncItemWithEntity(false);
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
        }
    }

    public ItemStack removeStack(int slot){
        if(slot == 0){
            ItemStack temp = storedItem;
            storedItem = ItemStack.EMPTY;
            syncItemWithEntity(true);
            return temp;
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeStack(int slot, int amount){
        if(slot == 0){
            ItemStack newSplit = storedItem.split(amount);
            syncItemWithEntity(true);
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
}
