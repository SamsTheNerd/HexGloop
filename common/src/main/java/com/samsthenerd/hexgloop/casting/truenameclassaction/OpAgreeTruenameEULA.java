package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.misc.worldData.AgreeTruenameEULAState;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpAgreeTruenameEULA implements ConstMediaAction {

    public static List<String> expectedSources = List.of("staff");

    public OpAgreeTruenameEULA(){
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
        CastSource source = context.getSource();
        if(source != CastSource.STAFF){ // if it's not a staff need to do special handling
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
        }
        ServerPlayerEntity player = context.getCaster();
        if(player == null) return new ArrayList<Iota>();
        AgreeTruenameEULAState eula = AgreeTruenameEULAState.getServerState(context.getWorld().getServer());
        // toggle it so you can unagree
        eula.setAgreement(player.getUuid(), !eula.checkAgreement(player.getUuid()));
        return new ArrayList<Iota>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}