package com.samsthenerd.hexgloop.casting.mirror;

import java.util.List;
import java.util.UUID;

import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.NullIota;
import at.petrak.hexcasting.api.spell.mishaps.MishapLocationTooFarAway;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class OpBindMirror implements ConstMediaAction {
    private boolean temp;

    public OpBindMirror(boolean temp){
        this.temp = temp;
    }

    @Override
    public int getArgc(){ return 1;}

    @Override
    public int getMediaCost(){
        return (temp ? 10 : 4 * MediaConstants.DUST_UNIT);
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
        if(!((Object)context instanceof IMirrorBinder binder)) return List.of();
        if(args.get(0) instanceof NullIota){
            // received null, want to unbind
            binder.bindTo(null, temp);
            return List.of();
        }
        Vec3d pos = OperatorUtils.getVec3(args, 0, getArgc());
        if(context.isVecInRange(pos)){
            BlockPos bPos = new BlockPos(pos);
            BlockEntityPedestal pedestal = context.getWorld().getBlockEntity(bPos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
            if(pedestal == null){
                // need to mishap about no pedestal found,, agony
                // MishapThrowerWrapper.throwMishap(new MishapBadBlock(bPos, HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get().getName()));
                binder.bindTo(null, temp);
            } else {
                UUID uuid = pedestal.getPersistentUUID();
                BoundMirror mirror = new BoundMirror(context.getWorld(), bPos, uuid);
                binder.bindTo(mirror, temp);
            }
        } else {
            // need to range mishap
            MishapThrowerWrapper.throwMishap(new MishapLocationTooFarAway(pos, "too_far"));
        }
        return List.of();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}
