package com.samsthenerd.hexgloop.casting.mirror;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class BoundMirror {
    // ok,, not sure we actually needed world + pos but oh well, neat to have if we want them later i guess
    private RegistryKey<World> dim;
    private BlockPos pos;
    private UUID uuid;

    public static final String NBT_KEY = "bound_mirror";

    public BoundMirror(RegistryKey<World> dim, BlockPos pos, UUID uuid) {
        this.dim = dim;
        this.pos = pos;
        this.uuid = uuid;
    }

    public BoundMirror(World world, BlockPos pos, UUID uuid) {
        this(world.getRegistryKey(), pos, uuid);
    }

    public RegistryKey<World> getDim() {
        return dim;
    }

    public BlockPos getPos() {
        return pos;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Nullable
    public ItemEntity getItemEntity(ServerWorld world){
        return getItemEntity(world.getServer());
    }

    @Nullable
    public ItemEntity getItemEntity(MinecraftServer server){
        ServerWorld targetWorld = server.getWorld(dim);
        Entity maybeItemEnt = targetWorld.getEntity(uuid);
        if(maybeItemEnt instanceof ItemEntity itemEnt){
            return itemEnt;
        }
        return null;
    }

    public ItemStack getItemStack(ServerWorld world){
        return getItemStack(world.getServer());
    }

    public ItemStack getItemStack(MinecraftServer server){
        ItemEntity itemEnt = getItemEntity(server);
        if(itemEnt != null){
            return itemEnt.getStack();
        }
        return ItemStack.EMPTY;
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
        nbt.putUuid("uuid", uuid);
        nbt.putLong("pos", pos.asLong());
        nbt.putString("dim", dim.getValue().toString());
        return nbt;
    }
    

    public static BoundMirror fromNbt(NbtCompound nbt){
        UUID uuid = nbt.containsUuid("uuid") ? nbt.getUuid("uuid") : null;
        BlockPos pos = nbt.contains("pos", NbtElement.LONG_TYPE) ? BlockPos.fromLong(nbt.getLong("pos")) : null;
        Identifier dimID = nbt.contains("dim", NbtElement.STRING_TYPE) ? new Identifier(nbt.getString("dim")) : null;
        RegistryKey<World> dim = dimID != null ? RegistryKey.of(Registry.WORLD_KEY, dimID) : null;
        if(uuid == null || pos == null || dim == null){
            return null;
        }
        return new BoundMirror(dim, pos, uuid);
    }
}
