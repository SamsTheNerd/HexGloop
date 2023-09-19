package com.samsthenerd.hexgloop.casting.mishapprotection;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.mojang.datafixers.util.Either;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.OperatorUtils;
import at.petrak.hexcasting.api.spell.SpellList;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.ContinuationFrame;
import at.petrak.hexcasting.api.spell.casting.eval.FrameEvaluate;
import at.petrak.hexcasting.api.spell.casting.eval.FrameFinishEval;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.PatternIota;
import at.petrak.hexcasting.api.spell.math.HexPattern;
import at.petrak.hexcasting.api.spell.mishaps.MishapNotEnoughArgs;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.text.Text;

public class OpEvalCatchMishap implements Action {
   @NotNull
   public static final OpEvalCatchMishap INSTANCE = new OpEvalCatchMishap();

   public static FrameFinishEval CATCHY_INSTANCE = ((ICatchyFrameEval)(Object)
        ((ICatchyFrameEval)(Object)(FrameFinishEval.INSTANCE)).initFromInstance()).setCatchy(true);

   private OpEvalCatchMishap() {
   }

   // mostly yoinked from OpEval
   public OperationResult operate(@NotNull SpellContinuation continuation, @NotNull List<Iota> stack, @Nullable Iota ravenmind, @NotNull CastingContext ctx) {
        Intrinsics.checkNotNullParameter(continuation, "continuation");
        Intrinsics.checkNotNullParameter(stack, "stack");
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Iota iotaToEval = (Iota)CollectionsKt.removeLastOrNull(stack);
        if (iotaToEval == null) {
            MishapThrowerWrapper.throwMishap(new MishapNotEnoughArgs(1, 0));
            // agony
            return null; // unreachable but kotlin is dumb
        } else {
            Either<HexPattern, SpellList> instrs = OperatorUtils.evaluatable(iotaToEval, 0);
            instrs.ifRight((instrsList) -> {
                ctx.incDepth();
            });
            SpellContinuation newCont = !instrs.left().isPresent() // false if it's a single pattern
                && (
                    !(continuation instanceof SpellContinuation.NotDone) // true only if it's done
                    || !(((SpellContinuation.NotDone)continuation).getFrame() instanceof FrameFinishEval) // true if the current (/next?) frame isn't a finish frame
                ) // true if it's done or doesn't have a finish frame
                ? continuation.pushFrame((ContinuationFrame)FrameFinishEval.INSTANCE) : continuation;
            // so sounds like ^^ is just making a new continuation finish frame if it's a list and there isn't currently one. - i guess just so it knows to eventually exit ?
            newCont = newCont.pushFrame((ContinuationFrame)CATCHY_INSTANCE);
            // wraps a single pattern in a spelllist or returns just the spelllist
            SpellList instrsList = (SpellList)instrs.map((singleInstr) -> new SpellList.LList(0, List.of(new PatternIota(singleInstr))), (spellList) -> spellList);
            Intrinsics.checkNotNullExpressionValue(instrsList, "instrsList"); // sure whatever kotlin
            // makes a frame eval thing to run these instructions
            FrameEvaluate frame = new FrameEvaluate(instrsList, true);
            return new OperationResult(newCont.pushFrame((ContinuationFrame)frame), stack, ravenmind, CollectionsKt.emptyList());
        }
   }

   public boolean isGreat() {
      return DefaultImpls.isGreat(this);
   }

   public boolean getAlwaysProcessGreatSpell() {
      return DefaultImpls.getAlwaysProcessGreatSpell(this);
   }

   public boolean getCausesBlindDiversion() {
      return DefaultImpls.getCausesBlindDiversion(this);
   }

   @NotNull
   public Text getDisplayName() {
      return DefaultImpls.getDisplayName(this);
   }
}