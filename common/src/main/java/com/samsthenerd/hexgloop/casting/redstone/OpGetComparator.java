package com.samsthenerd.hexgloop.casting.redstone;

import java.util.List;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import net.minecraft.block.BlockState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class OpGetComparator implements ConstMediaAction {
    private boolean onlyRedstone;

    // if onlyRedstone it will only read actual redstone power, not comparator values
    public OpGetComparator(boolean onlyRedstone){
        this.onlyRedstone = onlyRedstone;
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
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        context.assertVecInRange(pos);
        int power = context.getWorld().getReceivedRedstonePower(pos);
        if(!onlyRedstone){
            BlockState state = context.getWorld().getBlockState(pos);
            if(state.hasComparatorOutput()){
                power = state.getComparatorOutput(context.getWorld(), pos);
            }
        }
        return List.of(new DoubleIota(power));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}
