package com.samsthenerd.hexgloop.casting.mirror;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapICanOnlyCodeSoMuchPlsDontDupe extends Mishap{
    public MishapICanOnlyCodeSoMuchPlsDontDupe() {
        super();
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
            errorArgs = new Object[]{this.actionName(errorCtx.getAction())};
            errorText = this.error("mirror_limits", errorArgs);
        return errorText;
    }
}
