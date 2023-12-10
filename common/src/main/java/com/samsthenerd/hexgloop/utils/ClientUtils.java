package com.samsthenerd.hexgloop.utils;

import java.util.Map;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.samsthenerd.hexgloop.mixins.misc.MixinAccessClientAdvancementProgress;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientAdvancementManager;
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

    // doesn't account for enlightenment modifiers, rip i guess
    public static boolean isEnlightened(){
        ClientAdvancementManager manager = MinecraftClient.getInstance().getNetworkHandler().getAdvancementHandler();
        if(manager instanceof MixinAccessClientAdvancementProgress progressHolder){
            Map<Advancement, AdvancementProgress> progresses = progressHolder.getAdvancementProgresses();
            Advancement enlightenment = manager.getManager().get(HexAPI.modLoc("enlightenment"));
            if(progresses.containsKey(enlightenment)){
                return progresses.get(enlightenment).isDone();
            }
        }
        return false;
    }

    public static float getClientTime(){
        return MinecraftClient.getInstance().getTickDelta() + MinecraftClient.getInstance().player.getWorld().getTime();
    }
}
