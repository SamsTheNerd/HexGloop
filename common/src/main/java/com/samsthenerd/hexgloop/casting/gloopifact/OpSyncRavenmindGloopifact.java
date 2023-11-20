package com.samsthenerd.hexgloop.casting.gloopifact;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OpSyncRavenmindGloopifact implements Action {
    private boolean toOrFromStaff; //true if it's syncing gloopifact -> staff, false if it's syncing staff -> gloopifact
    
    public OpSyncRavenmindGloopifact(boolean toOrFromStaff){
        this.toOrFromStaff = toOrFromStaff;
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
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext context){
        ItemStack castHandStack = GloopifactUtils.assertGloopcasting(context).getLeft();
        Iota newRavenmind = ravenmind;
        // make sure we're actually casting from the gloopifact
        CastingHarness staffHarness = IXplatAbstractions.INSTANCE.getHarness(context.getCaster(), context.getOtherHand());
        if(toOrFromStaff){ // to staff
            PlayerEntity foundTruename = null;
            if(ravenmind != null) foundTruename = MishapOthersName.getTrueNameFromDatum(ravenmind, context.getCaster());
            if( foundTruename != null && foundTruename != context.getCaster()){
                MishapThrowerWrapper.throwMishap(new MishapOthersName(foundTruename));
            }
            staffHarness.setRavenmind(ravenmind);
            IXplatAbstractions.INSTANCE.setHarness(context.getCaster(), staffHarness);
        } else {
            newRavenmind = staffHarness.getRavenmind();
            PlayerEntity foundTruename = null;
            if(ravenmind != null) foundTruename = MishapOthersName.getTrueNameFromDatum(newRavenmind, context.getCaster());
            if( foundTruename != null && foundTruename != context.getCaster()){
                MishapThrowerWrapper.throwMishap(new MishapOthersName(foundTruename));
            }
        }
        return new OperationResult(continuation, stack, newRavenmind, new ArrayList<OperatorSideEffect>());
    }   
}