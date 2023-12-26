package com.samsthenerd.hexgloop.network;

import java.util.List;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemCastingFrog;
import com.samsthenerd.hexgloop.mixins.orchard.MixinServerPlayerOrchard;
import com.samsthenerd.hexgloop.network.booktweaks.BookScrollHandlers;
import com.samsthenerd.hexgloop.network.booktweaks.BookScrollHandlersClient;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.api.spell.math.HexDir;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class HexGloopNetwork {
    public static final Identifier CLEAR_GRID_PACKET_ID = new Identifier(HexGloop.MOD_ID, "clear_grid");
    public static final Identifier OPEN_CASTING_GRID_PACKET_ID = new Identifier(HexGloop.MOD_ID, "open_casting_grid");
    public static final Identifier CHANGE_WALL_SCROLL_ID = new Identifier(HexGloop.MOD_ID, "change_wall_scroll");
    public static final Identifier CLOSE_HEX_BOOK_ID = new Identifier(HexGloop.MOD_ID, "close_hex_book");
    public static final Identifier PROMPT_REPLACE_SCROLL_ID = new Identifier(HexGloop.MOD_ID, "prompt_replace_scroll");
    public static final Identifier S2C_GLOOPY_SERVER_ID = new Identifier(HexGloop.MOD_ID, "s2c_gloopy_server");

    public static final Identifier C2S_FROG_CASTING = new Identifier(HexGloop.MOD_ID, "c2s_frog_casting");


    public static final Identifier C2S_UPDATE_ORCHARD_ID = new Identifier(HexGloop.MOD_ID, "ctos_update_orchard"); // the important one

    public static void register(){
        // clears the grid - maybe not actually super useful
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CLEAR_GRID_PACKET_ID, (buf, context) -> {
            PlayerEntity player = context.getPlayer();
            if(!(player instanceof ServerPlayerEntity)) return;
            IXplatAbstractions.INSTANCE.clearCastingData((ServerPlayerEntity) player);
        });
        
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, OPEN_CASTING_GRID_PACKET_ID, (buf, context) -> {
            PlayerEntity player = context.getPlayer();
            if(!(player instanceof ServerPlayerEntity)) return;
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            boolean clear = buf.readBoolean();
            if(clear) IXplatAbstractions.INSTANCE.clearCastingData((ServerPlayerEntity) player);
            
            CastingHarness harness = IXplatAbstractions.INSTANCE.getHarness(serverPlayer, Hand.MAIN_HAND);
            List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatterns(serverPlayer);
            var descs = harness.generateDescs(); 

            IXplatAbstractions.INSTANCE.sendPacketToPlayer(serverPlayer,
                new MsgOpenSpellGuiAck(Hand.MAIN_HAND, patterns, descs.getFirst(), descs.getSecond(), descs.getThird(),
                    harness.getParenCount()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2S_UPDATE_ORCHARD_ID, (buf, context) -> {
            PlayerEntity player = context.getPlayer();
            if(!(player instanceof ServerPlayerEntity sPlayer)) return;
            String angleSig = buf.readString();
            boolean activated = buf.readBoolean();
            ((MixinServerPlayerOrchard)(Object)sPlayer).setAssociation(HexPattern.fromAngles(angleSig, HexDir.EAST), activated);
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CHANGE_WALL_SCROLL_ID, BookScrollHandlers::handleReplaceScroll);

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, C2S_FROG_CASTING, (buf, context) -> {
            PlayerEntity player = context.getPlayer();
            ItemStack frogStackIsh = buf.readItemStack();
            ItemStack frogStack = null;
            for(ItemStack maybeFrog : HexGloopItems.CASTING_FROG_ITEM.get().getEquippedFrogs(player)){
                if(ItemStack.areEqual(maybeFrog, frogStackIsh)){
                    frogStack = maybeFrog;
                    break;
                }
            }
            if(frogStack.getItem() instanceof ItemCastingFrog frogItem){
                frogItem.cast(frogStack, player);
            }
        });

        registerGloopyServerSender();
    }

    public static void registerClientSideOnly(){
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CLOSE_HEX_BOOK_ID, BookScrollHandlersClient::handleCloseClientBook);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, PROMPT_REPLACE_SCROLL_ID, BookScrollHandlersClient::handleReplaceScrollPrompt);
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, S2C_GLOOPY_SERVER_ID, ServerSideCheckClient::handleServerSideConfirmation);
    }

    private static void registerGloopyServerSender(){
        PlayerEvent.PLAYER_JOIN.register(player -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            NetworkManager.sendToPlayer(player, HexGloopNetwork.S2C_GLOOPY_SERVER_ID, buf);
        });
    }
}
