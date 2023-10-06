package com.samsthenerd.hexgloop.utils;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientUtils {
    public static PlayerEntity makeOtherClientPlayer(UUID uuid){
        return new OtherClientPlayerEntity(MinecraftClient.getInstance().world, 
                        new GameProfile(uuid, ""), null);
    }

    public static World getClientWorld(){
        return MinecraftClient.getInstance().world;
    }

    @Environment(EnvType.CLIENT)
    public static boolean shouldShowReflected(){
        return DiscoveryHandlers.hasLens(MinecraftClient.getInstance().player);
    }
}
