package com.samsthenerd.hexgloop.mixins.mishapprotection;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mishapprotection.ICatchyFrameEval;

import at.petrak.hexcasting.api.spell.casting.eval.FrameFinishEval;
import at.petrak.hexcasting.api.spell.iota.BooleanIota;
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

    // add false to the stack
    @WrapOperation(method="evaluate(Lat/petrak/hexcasting/api/spell/casting/eval/SpellContinuation;Lnet/minecraft/server/world/ServerWorld;Lat/petrak/hexcasting/api/spell/casting/CastingHarness;)Lat/petrak/hexcasting/api/spell/casting/CastingHarness$CastResult;",
    at=@At(value="INVOKE", target="kotlin/collections/CollectionsKt.toList (Ljava/lang/Iterable;)Ljava/util/List;"))
    public List<Object> addFalseToStack(Iterable<Object> originalStack, Operation<List<Object>> original){
        List<Object> newStack = original.call(originalStack);
        if(isCatchy()){
            newStack = new ArrayList<Object>(newStack);
            newStack.add(new BooleanIota(false));
        }
        return newStack;
    }
}
