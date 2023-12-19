package com.samsthenerd.hexgloop.casting.tags;

import java.util.List;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.text.Text;
import ram.talia.moreiotas.api.OperatorUtilsKt;


// requires moreiotas and is better with hexal
public class OpCheckTag implements ConstMediaAction{

    public OpCheckTag(){

    }

    @Override
    public int getArgc(){ return 2;}

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
        Iota inputIota = args.get(0);
        String tag = OperatorUtilsKt.getString(args, 1, getArgc());
        boolean hasTag = TagUtils.getTagChecker(inputIota, context).hasTag(tag);
        return List.of(new BooleanIota(hasTag));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}

