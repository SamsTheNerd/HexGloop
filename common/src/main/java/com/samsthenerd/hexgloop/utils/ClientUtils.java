package com.samsthenerd.hexgloop.utils;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.misc.DiscoveryHandlers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
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

    // for god knows why, something is deciding to try to call DyeableItem.getColor() on something that is very much not that
    // so we're quarantining that over here - didn't fix it but it's staying here
    public static int getStaffDyeColor(ItemStack stack){
        if(stack.getItem() instanceof DyeableItem dyeItem){
            return dyeItem.getColor(stack);
        }
        return 0xFF_FFFFFF;
    }

    // try bringing this out of the lambda ?
    public static int getIotaHolderColor(ItemStack stack){
        if(stack.getItem() instanceof IotaHolderItem iotaHolder){
            return iotaHolder.getColor(stack);
        }
        return 0xFF_FFFFFF;
    }
}
