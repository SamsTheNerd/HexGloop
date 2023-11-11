package com.samsthenerd.hexgloop.casting.mishapprotection;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.compat.moreIotas.MoreIotasMaybeIotas;

import at.petrak.hexcasting.api.spell.ConstMediaAction;
import at.petrak.hexcasting.api.spell.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.eval.SpellContinuation;
import at.petrak.hexcasting.api.spell.iota.Iota;
import dev.architectury.platform.Platform;
import net.minecraft.text.Text;

public class OpRevealLastMishap implements ConstMediaAction {
   private boolean revealOrString;

   public OpRevealLastMishap(boolean revealOrString) {
      this.revealOrString = revealOrString;
   }

   @Override
   public List<Iota> execute(List<? extends Iota> args, CastingContext ctx){
      Text mishapText = ((IMishapStorage)(Object)ctx).getLastMishap();
      Text wrapperText = Text.translatable("hexgloop.mishap.catch_wrapper" + (mishapText == null ? ".none" : ""), mishapText);
      List<Iota> result = new ArrayList<>();
      if(revealOrString){
         ctx.getCaster().sendMessageToClient(wrapperText, false);
      } else if(Platform.isModLoaded("moreiotas")){
         result.add(MoreIotasMaybeIotas.makeStringIota(wrapperText.getString()));
      }
      return result;
   }

   @Override
   public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
      return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
   }

   public int getMediaCost(){
      return 0;
   }

   public int getArgc(){
      return 0;
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