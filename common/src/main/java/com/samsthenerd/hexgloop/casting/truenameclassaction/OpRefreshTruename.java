package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.misc.worldData.TruenameLockState;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import dev.architectury.platform.Platform;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpRefreshTruename implements ConstMediaAction {

    public static List<String> expectedSources = List.of("staff", "packaged_hex");

    public OpRefreshTruename(){
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
            if(source != CastSource.PACKAGED_HEX){
                // throw mishap, it's something else
                MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
            } else {
                // need to check if it's a wisp or not
                if(Platform.isModLoaded("hexal")){
                    if(HexalWispWrapper.isWisp(context)){
                        // it's a wisp so don't let this cast it
                        MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
                    }
                }
            }

        }
        ServerPlayerEntity player = context.getCaster();
        if(player == null) return new ArrayList<Iota>();
        UUID newUuid = UUID.randomUUID();
        TruenameLockState.getServerState(context.getWorld().getServer()).setLockUUID(player.getUuid(), newUuid);
        return new ArrayList<Iota>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}