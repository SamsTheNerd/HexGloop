package com.samsthenerd.hexgloop.misc.worldData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class TruenameLockState extends PersistentState{
    // maps player uuids to their truename lock uuids
    private Map<UUID, UUID> uuidMap;

    @Nullable
    public UUID getLockUUID(UUID playerUUID){
        return uuidMap.get(playerUUID);
    }

    public void setLockUUID(UUID playerUUID, UUID lockUUID){
        uuidMap.put(playerUUID, lockUUID);
        markDirty();
    }

    public TruenameLockState(){
        super();
        uuidMap = new HashMap<UUID, UUID>();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound lockMapUUID = new NbtCompound();
        for(UUID playerUuid : uuidMap.keySet()){
            UUID lockUuid = uuidMap.get(playerUuid);
            lockMapUUID.putString(playerUuid.toString(), lockUuid.toString());
        }
        nbt.put("lockMap", lockMapUUID);
        return nbt;
    }
 
    public static TruenameLockState createFromNbt(NbtCompound tag) {
        TruenameLockState serverState = new TruenameLockState();
        NbtCompound lockMapNbt = tag.getCompound("lockMap");
        for(String playerUuidString : lockMapNbt.getKeys()){
            String lockUuidString = lockMapNbt.getString(playerUuidString);
            serverState.setLockUUID(UUID.fromString(playerUuidString), UUID.fromString(lockUuidString));
        }
        return serverState;
    }

    public static TruenameLockState getServerState(MinecraftServer server) {
        // First we get the persistentStateManager for the OVERWORLD
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();
 
        // Calling this reads the file from the disk if it exists, or creates a new one and saves it to the disk
        TruenameLockState serverState = persistentStateManager.getOrCreate(
                TruenameLockState::createFromNbt,
                TruenameLockState::new,
                HexGloop.MOD_ID + ":truename_lock_state"); 

        serverState.markDirty(); // YOU MUST DO THIS!!!! Or data wont be saved correctly.
 
        return serverState;
    }
}
