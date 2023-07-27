package com.samsthenerd.hexgloop.casting.orchard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.ListIota;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpReadOrchard implements ConstMediaAction{
    private boolean asList;

    public OpReadOrchard(boolean asList){
        this.asList = asList;
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
    public Text getDisplayName(){ return Text.translatable("hexgloop.spellaction.read_orchard");}

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingContext context){
        ServerPlayerEntity player = context.getCaster();
        if(player == null) return new ArrayList<Iota>();
        if(asList){
            List<Double> orchardList = ((IOrchard)player).getOrchardList();
            List<Iota> iotadList = orchardList.stream().map(val -> new DoubleIota(val)).collect(Collectors.toList());
            ListIota orchardIotaList = new ListIota(iotadList);
            return List.of(orchardIotaList);
        } else {
            return List.of(new DoubleIota(((IOrchard)player).getOrchardValue()));
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}
