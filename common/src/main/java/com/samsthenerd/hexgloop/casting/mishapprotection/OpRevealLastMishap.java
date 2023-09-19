package com.samsthenerd.hexgloop.casting.mishapprotection;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import at.petrak.hexcasting.api.spell.Action;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.RenderedSpell;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.casting.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.spell.iota.Iota;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.text.Text;

public class OpRevealLastMishap implements Action {
   @NotNull
   public static final OpRevealLastMishap INSTANCE = new OpRevealLastMishap();

   private OpRevealLastMishap() {
   }

    @NotNull
    public OperationResult operate(@NotNull SpellContinuation continuation, @NotNull List<Iota> stack, @Nullable Iota ravenmind, @NotNull CastingContext ctx) {
        Intrinsics.checkNotNullParameter(continuation, "continuation");
        Intrinsics.checkNotNullParameter(stack, "stack");
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        
        return new OperationResult(continuation, stack, ravenmind, CollectionsKt.listOf(new OperatorSideEffect.AttemptSpell((RenderedSpell)(new Spell(((IMishapStorage)(Object)ctx).getLastMishap())), false, false)));
    }

   private class Spell implements RenderedSpell {
    private Text mishapText;
    public Spell(Text mishapText) {
        this.mishapText = mishapText;
    }
    public void cast(CastingContext ctx) {
        Text wrapperText = Text.translatable("hexgloop.mishap.catch_wrapper" + (mishapText == null ? ".none" : ""), mishapText);
        ctx.getCaster().sendMessageToClient(wrapperText, false);
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