package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.misc.FrozenColorizer;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.mishaps.Mishap;
import dev.architectury.platform.Platform;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

// otherwise known as MishapBadCastingSource
public class MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh extends Mishap {
    private List<String> acceptedSources;

    // expects the list to match the source name suffixes from sourceFromCtx
    public MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List<String> acceptedSources) {
        super();
        this.acceptedSources = acceptedSources;
    }

    @NotNull
    public FrozenColorizer accentColor(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(errorCtx, "errorCtx");
        return this.dyeColor(DyeColor.BROWN);
    }

    public void execute(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx, @NotNull List<Iota> stack) {
        
    }

    public static String sourceFromCtx(CastingContext ctx){
        CastSource source = ctx.getSource();
        if(((IContextHelper)(Object)ctx).isKitty()){
            return "inventorty";
        }
        if(source == CastSource.STAFF){
            return "staff";
        } else if(source == CastSource.SPELL_CIRCLE){
            return "circle";
        } else if(source == CastSource.PACKAGED_HEX){
            if(Platform.isModLoaded("hexal")){
                if(HexalWispWrapper.isWisp(ctx)){
                    return "wisp";
                }
            }
            ItemStack castHandStack = ctx.getCaster().getStackInHand(ctx.getCastingHand());
            if(castHandStack.getItem() instanceof ItemGloopifact gloopifactItem){
                return "gloopifact";
            }
            return "packaged_hex";
        }
        return "unknown";
    }

    private Text expectedSourcesList(){
        MutableText list = Text.literal("");
        int i = 0;
        for(String source : this.acceptedSources){
            list.append(Text.translatable("hexgloop.source_type."+source));
            i++;
            if(i != this.acceptedSources.size()){
                list.append(Text.literal(", "));
            }
        }
        return list;
    }

    @NotNull
    public Text errorMessage(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        Text errorText;
        Object[] errorArgs;
            errorArgs = new Object[]{this.actionName(errorCtx.getAction()), expectedSourcesList(), Text.translatable("hexgloop.source_type."+sourceFromCtx(ctx))};
            errorText = this.error("wrong_casting_source" + (acceptedSources.size() > 1 ? "s" : "" ), errorArgs);
        return errorText;
    }
}
