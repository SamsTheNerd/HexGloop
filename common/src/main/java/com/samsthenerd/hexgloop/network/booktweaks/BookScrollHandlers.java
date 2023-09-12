package com.samsthenerd.hexgloop.network.booktweaks;

import java.util.UUID;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.network.HexGloopNetwork;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import at.petrak.hexcasting.common.items.ItemScroll;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class BookScrollHandlers {
    public static void handleReplaceScroll(PacketByteBuf buf, PacketContext context){
        HexGloop.logPrint("received wall scroll change");
            PlayerEntity player = context.getPlayer();
            if(!(player instanceof ServerPlayerEntity)) return;
            HexGloop.logPrint("got a server player");
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            UUID scrollUUID = buf.readUuid();
            NbtCompound patternNbt = buf.readNbt();
            HexPattern pattern = null;
            try{
                pattern = HexPattern.fromNBT(patternNbt);
            } catch (Exception e){
                HexGloop.logPrint("failed to parse pattern");
                e.printStackTrace();
                return;
            }
            if(pattern == null) return;
            Entity maybeWallScroll = serverPlayer.getWorld().getEntity(scrollUUID);
            HexGloop.logPrint("got down to a maybe scroll");
            if(maybeWallScroll == null){
                HexGloop.logPrint("maybe scroll is null");
                return;
            }
            if(maybeWallScroll == null || serverPlayer.getPos().distanceTo(maybeWallScroll.getPos()) > 32) return;
            HexGloop.logPrint("we have a valid maybe wall scroll");
            if(maybeWallScroll instanceof EntityWallScroll wallScroll){
                // do this up here to try to get every tiny bit of time we can ?
                NetworkManager.sendToPlayer(serverPlayer, HexGloopNetwork.CLOSE_HEX_BOOK_ID, new PacketByteBuf(Unpooled.buffer()));
                ItemStack newScroll = wallScroll.scroll.copy();
                HexGloop.logPrint("changeing out scroll now");
                EntityWallScroll newWallScroll = new EntityWallScroll(wallScroll.getWorld(), wallScroll.getBlockPos(),
                    wallScroll.getHorizontalFacing(), newScroll, wallScroll.getShowsStrokeOrder(), wallScroll.blockSize);
                newWallScroll.setUuid(wallScroll.getUuid()); // keep the same uuid so we don't break hexes that rely on it
                NBTHelper.putCompound(newScroll, ItemScroll.TAG_PATTERN, pattern.serializeToNBT());
                wallScroll.discard();
                serverPlayer.world.spawnEntity(newWallScroll);
                // wallScroll.scroll = newScroll;
                // IXplatAbstractions.INSTANCE.sendPacketNear(wallScroll.getPos(), 32.0, serverPlayer.getWorld(),
                //     new MsgRecalcWallScrollDisplayAck(wallScroll.getId(), wallScroll.getShowsStrokeOrder()));
            }
    }
}
