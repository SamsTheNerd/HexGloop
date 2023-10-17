package com.samsthenerd.hexgloop.casting;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.mirror.IPlayerPTUContext;
import com.samsthenerd.hexgloop.casting.mirror.MishapICanOnlyCodeSoMuchPlsDontDupe;
import com.samsthenerd.hexgloop.items.ItemAbstractPassThrough.PassThroughUseContext;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class OpHotbar implements ConstMediaAction{
    // should probably just implement these as separate op classes but bleh
    private boolean isFlip; // if it's swapping main hand with offhand
    private boolean isRead; // if it's reading the hotbar or setting it

    public OpHotbar(boolean isFlip, boolean isRead){
        this.isFlip = isFlip;
        this.isRead = isRead;
    }

    @Override
    public int getArgc(){ return isFlip || isRead ? 0 : 1;}

    @Override
    public int getMediaCost(){
        return isFlip || !isRead ? MediaConstants.DUST_UNIT / 8 : 0;
    }

    @Override
    public boolean isGreat(){ return false;}

    @Override
    public boolean getCausesBlindDiversion(){ return false;}

    @Override 
    public boolean getAlwaysProcessGreatSpell(){ return false;}

    @Override
    public Text getDisplayName(){ 
        return DefaultImpls.getDisplayName(this);
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingContext context){
        ServerPlayerEntity player = context.getCaster();
        PassThroughUseContext<?,?> ptuContext = ((IPlayerPTUContext)(Object)player).getPTUContext();
        if(isFlip){
            if(ptuContext != null){
                // mishap for dupe reasons / stability reasons
                MishapThrowerWrapper.throwMishap(new MishapICanOnlyCodeSoMuchPlsDontDupe());
                return new ArrayList<>();
            }
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getOffHandStack();
            player.setStackInHand(Hand.MAIN_HAND, offHand);
            player.setStackInHand(Hand.OFF_HAND, mainHand);
            if(ptuContext != null && ptuContext.ptSlot == player.getInventory().selectedSlot){
                ptuContext.ptSlot = PlayerInventory.OFF_HAND_SLOT;
            } else if(ptuContext != null && ptuContext.ptSlot == PlayerInventory.OFF_HAND_SLOT){
                ptuContext.ptSlot = player.getInventory().selectedSlot;
            }
        } else {
            if(isRead){
                int selectedSlot = player.getInventory().selectedSlot;
                return List.of(new DoubleIota(selectedSlot));
            } else {
                Double newSlot = OperatorUtils.getDoubleBetween(args, 0, 0, 8, getArgc());
                player.getInventory().selectedSlot = newSlot.intValue();
                player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(player.getInventory().selectedSlot));
            }
        }
        return new ArrayList<>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}


