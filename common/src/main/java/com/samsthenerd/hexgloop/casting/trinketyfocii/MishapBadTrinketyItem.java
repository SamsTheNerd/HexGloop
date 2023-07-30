package com.samsthenerd.hexgloop.casting.trinketyfocii;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import at.petrak.hexcasting.api.utils.HexUtils;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

// mostly copied from MishapBadOffhandItem
public final class MishapBadTrinketyItem extends Mishap {
    private final ItemStack item;
    private final String slot;
    private final Text wanted;

    public MishapBadTrinketyItem(@NotNull ItemStack item, @NotNull String slot, @NotNull Text wanted) {
      super();
      this.item = item;
      this.slot = slot;
      this.wanted = wanted;
   }

   @NotNull
   public final ItemStack getItem() {
      return this.item;
   }

   @NotNull
   public final String getSlot() {
      return this.slot;
   }

   @NotNull
   public final Text getWanted() {
      return this.wanted;
   }

   @NotNull
   public FrozenColorizer accentColor(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
      Intrinsics.checkNotNullParameter(ctx, "ctx");
      Intrinsics.checkNotNullParameter(errorCtx, "errorCtx");
      return this.dyeColor(DyeColor.BROWN);
   }

   public void execute(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx, @NotNull List<Iota> stack) {
      
   }

   @NotNull
   public Text errorMessage(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
      Text errorText;
      Object[] errorArgs;
      if (this.item.isEmpty()) {
         errorArgs = new Object[]{this.actionName(errorCtx.getAction()), this.wanted, Text.translatable("hexgloop.slotname."+this.slot)};
         errorText = this.error("no_item.slot", errorArgs);
      } else {
         errorArgs = new Object[]{this.actionName(errorCtx.getAction()), this.wanted, Text.translatable("hexgloop.slotname."+this.slot), this.item.getCount(), this.item.toHoverableText()};
         errorText = this.error("bad_item.slot", errorArgs);
      }

      return errorText;
   }

   public final static MishapBadTrinketyItem of(@NotNull ItemStack item, @NotNull String slot, @NotNull String stub, @NotNull Object... args) {
      return new MishapBadTrinketyItem(item, slot, (Text)HexUtils.asTranslatedComponent("hexcasting.mishap.bad_item." + stub, Arrays.copyOf(args, args.length)));
   }
}