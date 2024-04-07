package com.samsthenerd.hexgloop.casting.canvas;

import java.util.List;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.DoubleIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.MapColor.Brightness;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class OpGetBlockColor implements ConstMediaAction{

    public OpGetBlockColor(){
    }

    @Override
    public int getArgc(){ return 1; }

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
        Brightness brightness = Brightness.NORMAL;
        int argOffset = 0;
        if(args.get(0) instanceof DoubleIota doubleIota){
            double brightnessVal = doubleIota.getDouble();
            if(brightnessVal >= 0 && brightnessVal <= 3){
                brightness = Brightness.validateAndGet((int)brightnessVal);
                argOffset = 1;
            }
        }
        BlockPos pos = OperatorUtils.getBlockPos(args, argOffset, getArgc());
        BlockState state = context.getWorld().getBlockState(pos);
        MapColor mapColor = state.getMapColor(context.getWorld(), pos);
        int rgb = mapColor.getRenderColor(brightness);
        return List.of(new Vec3Iota(new Vec3d((rgb >>> 16) & 0xFF, (rgb >>> 8) & 0xFF, rgb & 0xFF)));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}
