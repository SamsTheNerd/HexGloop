package com.samsthenerd.hexgloop.mixins.mishapprotection;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.casting.mishapprotection.ICatchyFrameEval;

import at.petrak.hexcasting.api.spell.casting.eval.FrameFinishEval;
import at.petrak.hexcasting.api.spell.iota.Iota;
import kotlin.Pair;

@Mixin(FrameFinishEval.class)
public class MixinCatchyFrameEval implements ICatchyFrameEval {
    private boolean catchy = false;

    @Invoker("<init>")
    static FrameFinishEval init() {
        throw new AssertionError();
    }

    public FrameFinishEval initFromInstance(){
        return init();
    }

    public boolean isCatchy(){
        return catchy;
    }

    public FrameFinishEval setCatchy(boolean catchy){
        this.catchy = catchy;
        return (FrameFinishEval)(Object)this;
    }

    @Inject(method="breakDownwards(Ljava/util/List;)Lkotlin/Pair;", at=@At("HEAD"), cancellable=true, remap = false)
    public void cancelBreakDownwardsForCatchy(List<Iota> stack, CallbackInfoReturnable<Pair<Boolean, List<Iota>>> cir){
        if(isCatchy()){
            cir.setReturnValue(new Pair<>(false, stack));
        }
    }
}
