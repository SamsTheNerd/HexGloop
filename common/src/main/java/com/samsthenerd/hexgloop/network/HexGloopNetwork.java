package com.samsthenerd.hexgloop.network;

import java.util.List;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.ResolvedPattern;
import at.petrak.hexcasting.common.network.MsgOpenSpellGuiAck;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.networking.NetworkManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class HexGloopNetwork {
    public static final Identifier CLEAR_GRID_PACKET_ID = new Identifier(HexGloop.MOD_ID, "clear_grid");
    public static final Identifier OPEN_CASTING_GRID_PACKET_ID = new Identifier(HexGloop.MOD_ID, "open_casting_grid");

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


    }
}
