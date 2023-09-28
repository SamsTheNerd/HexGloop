package com.samsthenerd.hexgloop.casting.mishapprotection;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs;
import net.minecraft.text.Text;

public class OpHahaFunnyAssertQEDGetItLikeTheMathProofLol implements Action {

    public OpHahaFunnyAssertQEDGetItLikeTheMathProofLol(){
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
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        if(stack.size() < 1){
            // need to mishap about not enough args
            MishapThrowerWrapper.throwMishap(new MishapNotEnoughArgs(1, 0));
            return new OperationResult(continuation, stack, ravenmind, new ArrayList<OperatorSideEffect>());
        }
        Iota firstArg = stack.get(stack.size()-1);
        stack.remove(stack.size()-1); // remove the first iota, whatever it is
        Text mishapLabelText = null;
        boolean shouldMishap = false;
        if(firstArg instanceof BooleanIota bIota){
            shouldMishap = !bIota.getBool();
        } else {
            mishapLabelText = firstArg.display();
            if(stack.size() < 1){
                // need to mishap about lack of second arg
                MishapThrowerWrapper.throwMishap(new MishapNotEnoughArgs(2, 1));
                return new OperationResult(continuation, stack, ravenmind, new ArrayList<OperatorSideEffect>());
            }
            Iota secondArg = stack.get(stack.size()-1);
            stack.remove(stack.size()-1); // remove the second iota too if we're checking it
            if(secondArg instanceof BooleanIota bIota2){
                shouldMishap = !bIota2.getBool();
            } else {
                // need to mishap about wrong iota type
                MishapThrowerWrapper.throwMishap(MishapInvalidIota.ofType(secondArg, stack.size()-2, "boolean"));
                return new OperationResult(continuation, stack, ravenmind, new ArrayList<OperatorSideEffect>());
            }
        }
        if(shouldMishap){
            MishapThrowerWrapper.throwMishap(new MishapAssertion(mishapLabelText));
        }
        return new OperationResult(continuation, stack, ravenmind, new ArrayList<OperatorSideEffect>());
    }   
}
