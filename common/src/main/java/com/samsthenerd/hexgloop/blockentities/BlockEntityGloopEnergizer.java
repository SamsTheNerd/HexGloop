package com.samsthenerd.hexgloop.blockentities;

import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.blocks.BlockGloopEnergizer;
import com.samsthenerd.hexgloop.recipes.GloopingRecipes;
import com.samsthenerd.hexgloop.recipes.GloopingRecipes.GloopingRecipe;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexItems;
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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BlockEntityGloopEnergizer extends BlockEntity implements Inventory{
    private static final int MAX_CAPACITY = 2_000_000_000;
    private static final double DUST_COST = 0.25; // per second per block
    private static final double PARTICLE_SPAWN_CHANCE = 0.02;
    private static final ParticleEffect[] POSSIBLE_PARTICLES = {ParticleTypes.SNEEZE, ParticleTypes.EFFECT};
    Random rand = new Random();
    protected ItemStack latestResult = ItemStack.EMPTY;
    private int numConnectedBlocks = 0;
    private int media = 0;
    
    public BlockEntityGloopEnergizer(BlockPos pos, BlockState state) {
        super(HexGloopBEs.GLOOP_ENERGIZER_BE.get(), pos, state);
    }

    // tries to find a recipe it can make and makes it
    public void makeGloopSoup(BlockPos centerPos){
        if(!(world instanceof ServerWorld)) return;
        if(this.media < MediaConstants.DUST_UNIT * 10) return;
        GloopingRecipe bestRec = detectRecipe();
        if(bestRec == null) return;
        this.media -= MediaConstants.DUST_UNIT * 10;
        Box box = new Box(pos).expand((double) BlockGloopEnergizer.BOILER_RADIUS);
        List<Entity> potentialIngredients = world.getOtherEntities(null, box);
        doGloopCraft(bestRec, potentialIngredients, centerPos);
    }

    public Entity doGloopCraft(GloopingRecipe rec, List<Entity> potentialIngredients, BlockPos spawnPos){
        if(!(world instanceof ServerWorld)) return null;
        if(rec == null) return null;
        ItemStack result = rec.craft(potentialIngredients, true).copy();
        if(result.isEmpty()) return null;
        result.setCount(1);
        ItemEntity yummySoup = new ItemEntity(world, spawnPos.getX() + 0.5, spawnPos.getY() + 1.5, spawnPos.getZ() + 0.5, result);
        yummySoup.setInvulnerable(true);
        boolean didSpawn = world.spawnEntity(yummySoup);
        // HexGloop.logPrint("spawned gloop soup product: " + didSpawn + "; " + yummySoup.getUuidAsString() + " at " + yummySoup.getPos().toString());
        return yummySoup;
        // return EntityType.ITEM.spawnFromItemStack((ServerWorld)world, result, null, spawnPos, SpawnReason.EVENT, true, false);
    }

    public GloopingRecipe detectRecipe(){
        Box box = new Box(pos).expand((double) BlockGloopEnergizer.BOILER_RADIUS);
        List<Entity> potentialIngredients = world.getOtherEntities(null, box);
        GloopingRecipe rec = GloopingRecipes.findRecipe(potentialIngredients, world.getRecipeManager());
        latestResult = rec == null ? ItemStack.EMPTY : rec.getOutput();
        sync();
        return rec;
    }

    // want to use up media and also spawn some particles
    public void tick(World world, BlockPos pos, BlockState state){
        if(!(world instanceof ServerWorld)) return;
        if(this.media > 0 || this.media == -1){
            Set<BlockPos> adjWater = BlockGloopEnergizer.getAdjacentWater(world, pos);
            numConnectedBlocks = adjWater.size();
            if(!adjWater.isEmpty()){
                double decAmt = MediaConstants.DUST_UNIT / 20.0;
                this.media = (int) Math.max(((double)this.media) - decAmt, 0);
                for(BlockPos waterPos : adjWater){
                    if(rand.nextDouble() > PARTICLE_SPAWN_CHANCE) continue;
                    ((ServerWorld) world).spawnParticles(ParticleTypes.SNEEZE, 
                        waterPos.getX() + 0.5 + rand.nextDouble()*0.5, waterPos.getY() + 0.3 + rand.nextDouble()*0.5, waterPos.getZ()+0.5 + rand.nextDouble()*0.5, 1, 0, 0.3, 0, 0.01);
                }
            }
            sync();
        }
    }

    public ItemStack getLatestResult(){
        return latestResult;
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

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.media = nbt.getInt("media");
        this.numConnectedBlocks = nbt.getInt("numConnectedBlocks");
        this.latestResult = ItemStack.fromNbt(nbt.getCompound("latestResult"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("media", this.media);
        nbt.putInt("numConnectedBlocks", this.numConnectedBlocks);
        nbt.put("latestResult", this.latestResult.writeNbt(new NbtCompound()));
    }

    public int getNumConnected(){
        return this.numConnectedBlocks;
    }

    
    // yoink inventory/media stuff from impetus
    private static final int[] SLOTS = {0};

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getStack(int index) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public ItemStack removeStack(int index) {
        return ItemStack.EMPTY.copy();
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        insertMedia(stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void clear() {
        // NO-OP
    }

    public int getMedia() {
        return this.media;
    }

    public void setMedia(int media) {
        this.media = media;
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        if (remainingMediaCapacity() == 0) {
            return false;
        }

        if (stack.isOf(HexItems.CREATIVE_UNLOCKER)) {
            return true;
        }

        var mediamount = extractMediaFromItem(stack, true);
        return mediamount > 0;
    }

    public int extractMediaFromItem(ItemStack stack, boolean simulate) {
        if (this.media < 0) {
            return 0;
        }
        return MediaHelper.extractMedia(stack, remainingMediaCapacity(), true, simulate);
    }

    public void sync() {
        this.markDirty();
        this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void insertMedia(ItemStack stack) {
        if (getMedia() >= 0 && !stack.isEmpty() && stack.getItem() == HexItems.CREATIVE_UNLOCKER) {
            setInfiniteMedia();
            stack.decrement(1);
        } else {
            var mediamount = extractMediaFromItem(stack, false);
            if (mediamount > 0) {
                this.media = Math.min(mediamount + media, MAX_CAPACITY);
                this.sync();
            }
        }
    }

    public void setInfiniteMedia() {
        this.media = -1;
        this.sync();
    }

    public int remainingMediaCapacity() {
        if (this.media < 0) {
            return 0;
        }
        return Math.max(0, MAX_CAPACITY - this.media);
    }
}
