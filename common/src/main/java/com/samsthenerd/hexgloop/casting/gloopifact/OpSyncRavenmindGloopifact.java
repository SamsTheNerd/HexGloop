package com.samsthenerd.hexgloop.casting.gloopifact;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.HexalWispWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.spell.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.platform.Platform;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OpSyncRavenmindGloopifact implements Action {
    public static List<String> expectedSources = List.of("gloopifact");
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
        ItemStack castHandStack = context.getCaster().getStackInHand(context.getCastingHand());
        Iota newRavenmind = ravenmind;
        if(castHandStack.getItem() instanceof ItemGloopifact gloopifactItem){
            // make sure we're actually casting from the gloopifact
            if(context.getSource() == CastSource.PACKAGED_HEX){
                // verify that it's not a wisp and we should be fine ?
                boolean isWisp = false;
                if(Platform.isModLoaded("hexal")){
                    isWisp = HexalWispWrapper.isWisp(context);
                }
                if(!isWisp){
                    // good to go
                    // not sure how much hand matters for this ?
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
                    // ICADHarnessStorage storage = (ICADHarnessStorage)(Object)context.getCaster();
                    // CastingHarness thisHarness = storage.getHarness(context);
                    // CastingHarness syncTo = toOrFromStaff ? staffHarness : thisHarness;
                    // CastingHarness syncFrom = toOrFromStaff ? thisHarness : staffHarness;
                    // syncTo.setRavenmind(syncFrom.getRavenmind());
                    // newRavenmind = thisHarness.getRavenmind();
                }
            } else {
                // mishap wrong source
                MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
            }
        } else {
            MishapThrowerWrapper.throwMishap(MishapBadOffhandItem.of(castHandStack, context.getCastingHand(), "gloopifact"));
        }
        return new OperationResult(continuation, stack, newRavenmind, new ArrayList<OperatorSideEffect>());
    }   
}