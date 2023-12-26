package com.samsthenerd.hexgloop.network;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.HexGloopClient;

import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.networking.NetworkManager.PacketContext;
import net.minecraft.network.PacketByteBuf;
import vazkii.patchouli.api.PatchouliAPI;

public class ServerSideCheckClient {
    public static void handleServerSideConfirmation(PacketByteBuf buf, PacketContext context){
        HexGloopClient.isServerSide = true;
        PatchouliAPI.get().setConfigFlag("hexgloop:gloopyserver", true);
        HexGloop.logPrint("gloop is on server");
    }

    public static void registerDisconnectUpdate(){
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(player -> {
            HexGloopClient.isServerSide = false;
            PatchouliAPI.get().setConfigFlag("hexgloop:gloopyserver", false);
            HexGloop.logPrint("gloop is not on server");
        });
    }
}
