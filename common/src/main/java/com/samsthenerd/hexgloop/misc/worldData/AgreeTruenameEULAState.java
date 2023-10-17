package com.samsthenerd.hexgloop.misc.worldData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

// i guess could also just store on player nbt but nah
public class AgreeTruenameEULAState extends PersistentState{
    // maps player uuids to their truename lock uuids
    private Set<UUID> playersWhoAgreed;

    @Nullable
    public boolean checkAgreement(UUID playerUUID){
        return playersWhoAgreed.contains(playerUUID);
    }

    public void setAgreement(UUID playerUUID, boolean agreed){
        if(agreed)
            playersWhoAgreed.add(playerUUID);
        else
            playersWhoAgreed.remove(playerUUID);
        markDirty();
    }

    public AgreeTruenameEULAState(){
        super();
        playersWhoAgreed = new HashSet<>();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList agreedPlayers = new NbtList();
        for(UUID playerUuid : playersWhoAgreed){
            agreedPlayers.add(NbtString.of(playerUuid.toString()));
        }
        nbt.put("agreedPlayers", agreedPlayers);
        return nbt;
    }
 
    public static AgreeTruenameEULAState createFromNbt(NbtCompound tag) {
        AgreeTruenameEULAState serverState = new AgreeTruenameEULAState();
        NbtList agreedPlayers = tag.getList("agreedPlayers", NbtElement.STRING_TYPE);
        for(NbtElement agreedPlayer : agreedPlayers){
            serverState.setAgreement(UUID.fromString(agreedPlayer.asString()), true);
        }
        return serverState;
    }

    public static AgreeTruenameEULAState getServerState(MinecraftServer server) {
        // First we get the persistentStateManager for the OVERWORLD
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();
 
        // Calling this reads the file from the disk if it exists, or creates a new one and saves it to the disk
        AgreeTruenameEULAState serverState = persistentStateManager.getOrCreate(
                AgreeTruenameEULAState::createFromNbt,
                AgreeTruenameEULAState::new,
                HexGloop.MOD_ID + ":truename_eula_state"); 

        serverState.markDirty(); // YOU MUST DO THIS!!!! Or data wont be saved correctly.
 
        return serverState;
    }
}
