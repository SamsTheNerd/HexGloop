package com.samsthenerd.hexgloop.network.booktweaks;



import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.misc.GetPatchouliPatterns;
import com.samsthenerd.hexgloop.network.HexGloopNetwork;

import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.NetworkManager.PacketContext;
import io.netty.buffer.Unpooled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

public class BookScrollHandlersClient {
    public static void handleCloseClientBook(PacketByteBuf buf, PacketContext context){
        // should be fine ? maybe build in some more protections if it's an issue
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.currentScreen instanceof GuiBook){
            mc.currentScreen.close();
            mc.setScreen(null);
        }
    }

    public static void handleReplaceScrollPrompt(PacketByteBuf buf, PacketContext context){
        PlayerEntity player = context.getPlayer();
        Hand hand = buf.readBoolean() ? Hand.MAIN_HAND : Hand.OFF_HAND;
        ItemStack stack = player.getStackInHand(hand);
        Book book = ItemModBook.getBook(stack);
        if(book == null) return;
        GuiBook currentGui = book.getContents().getCurrentGui();
        if(currentGui instanceof GuiBookEntry guiEntry){
            BookEntry entry = guiEntry.getEntry();
            int spread = guiEntry.getSpread();
            Pair<HexPattern, HexPattern> patterns = GetPatchouliPatterns.getPatternsFromEntry(entry, spread);
            if(patterns.getLeft() == null && patterns.getRight() == null) return;
            // atleast one must be non-null
            HexPattern patternToUse = patterns.getLeft();
            if((hand == Hand.MAIN_HAND && patterns.getRight() != null) || patternToUse == null){
                patternToUse = patterns.getRight();
            }
            HexGloop.logPrint("got pattern: " + patternToUse.toString());
            MinecraftClient mc = MinecraftClient.getInstance();
            Entity maybeWallScroll = null;
            if(mc.crosshairTarget.getType() == HitResult.Type.ENTITY){
                EntityHitResult entResult = (EntityHitResult) mc.crosshairTarget;
                float reachDist = mc.interactionManager.getReachDistance();
                if(mc.crosshairTarget.squaredDistanceTo(player) > (reachDist * reachDist)){
                    return; // don't do anything if the player is too far away
                }
                maybeWallScroll = entResult.getEntity();
            }
            if(maybeWallScroll instanceof EntityWallScroll wallScroll){
                PacketByteBuf bufC2S = new PacketByteBuf(Unpooled.buffer());
                HexGloop.logPrint("correctly got a wall scroll !");
                bufC2S.writeUuid(wallScroll.getUuid());
                bufC2S.writeNbt(patternToUse.serializeToNBT());
                NetworkManager.sendToServer(HexGloopNetwork.CHANGE_WALL_SCROLL_ID, bufC2S);
                return;
            }
        }
    }
}
