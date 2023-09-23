package com.samsthenerd.hexgloop.casting.gloopifact;


import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.HexalWispWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.MishapBadOffhandItem;
import dev.architectury.platform.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class OpWriteGloopifact implements ConstMediaAction {
    public static List<String> expectedSources = List.of("gloopifact");
    private boolean simulate;
    
    public OpWriteGloopifact(boolean simulate){
        this.simulate = simulate;
    }

    @Override
    public int getArgc(){ return 0;}

    @Override
    public int getMediaCost(){
        return 0;
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
        ItemStack castHandStack = context.getCaster().getStackInHand(context.getCastingHand());
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
                    Iota iota = args.get(0);

                    boolean canWrite = gloopifactItem.canWrite(castHandStack, iota);
                    if(simulate) return List.of(new BooleanIota(canWrite));
                    if(!canWrite){
                        MishapThrowerWrapper.throwMishap(MishapBadOffhandItem.of(castHandStack, context.getCastingHand(), "iota.write", new Object[0]));
                        return List.of();
                    }
                    gloopifactItem.writeDatum(castHandStack, iota);
                }
            }
            // mishap wrong source
            if(simulate) return List.of(new BooleanIota(false));
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
        } else {
            if(simulate) return List.of(new BooleanIota(false));
            MishapThrowerWrapper.throwMishap(MishapBadOffhandItem.of(castHandStack, context.getCastingHand(), "gloopifact"));
        }
        return List.of();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }   
}