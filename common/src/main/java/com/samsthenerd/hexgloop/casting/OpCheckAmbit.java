package com.samsthenerd.hexgloop.casting;

import java.util.List;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota;
import net.minecraft.text.Text;

public class OpCheckAmbit implements ConstMediaAction{

    public OpCheckAmbit(){

    }

    @Override
    public int getArgc(){ return 1;}

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
        Iota firstArg = args.get(0);
        if(firstArg instanceof Vec3Iota vIota){
            return List.of(new BooleanIota(context.isVecInRange(vIota.getVec3())));
        }
        if(firstArg instanceof EntityIota eIota){
            return List.of(new BooleanIota(context.isEntityInRange(eIota.getEntity())));
        }
        Mishap m = MishapInvalidIota.ofType(firstArg, 0, "vector");
        MishapThrowerWrapper.throwMishap(m);
        return List.of(new BooleanIota(false));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}

