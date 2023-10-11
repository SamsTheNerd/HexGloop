package com.samsthenerd.hexgloop.casting;

import java.util.ArrayList;
import java.util.List;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.item.ItemStack;
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
        if(isFlip){
            ItemStack mainHand = context.getCaster().getMainHandStack();
            ItemStack offHand = context.getCaster().getOffHandStack();
            context.getCaster().setStackInHand(Hand.MAIN_HAND, offHand);
            context.getCaster().setStackInHand(Hand.OFF_HAND, mainHand);
        } else {
            if(isRead){
                int selectedSlot = context.getCaster().getInventory().selectedSlot;
                return List.of(new DoubleIota(selectedSlot));
            } else {
                Double newSlot = OperatorUtils.getDoubleBetween(args, 0, 0, 9, getArgc());
                context.getCaster().getInventory().selectedSlot = newSlot.intValue();
            }
        }
        return new ArrayList<>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}


