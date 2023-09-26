package com.samsthenerd.hexgloop.casting.mishapprotection;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapAssertion extends Mishap {
    Text errorLabel = null;

    public MishapAssertion(Text errorLabel) {
        super();
        this.errorLabel = errorLabel;
    }

    @NotNull
    public FrozenColorizer accentColor(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(errorCtx, "errorCtx");
        return this.dyeColor(DyeColor.BROWN);
    }

    public void execute(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx, @NotNull List<Iota> stack) {
        
    }

    public Text makeError(Text actionName, Text label){
        Object[] errorArgs;
        if(label == null){
            errorArgs = new Object[]{actionName};
            return this.error("assertion", errorArgs);
        } else {
            errorArgs = new Object[]{actionName, label};
            return this.error("assertion.labeled", errorArgs);
        }
    }

    @NotNull
    public Text errorMessage(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        return this.makeError(this.actionName(errorCtx.getAction()), errorLabel);
    }

}