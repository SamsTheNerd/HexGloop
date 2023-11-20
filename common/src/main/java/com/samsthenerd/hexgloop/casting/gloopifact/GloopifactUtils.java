package com.samsthenerd.hexgloop.casting.gloopifact;

import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.HexalWispWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingContext.CastSource;
import dev.architectury.platform.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class GloopifactUtils {
    public static List<String> expectedSources = List.of("gloopifact");

    public static Pair<ItemStack, ItemGloopifact> assertGloopcasting(CastingContext context){
        ItemStack castHandStack = context.getCaster().getStackInHand(context.getCastingHand());
        // make sure we're casting from atleast some packaged hex
        if(context.getSource() != CastSource.PACKAGED_HEX
        || (Platform.isModLoaded("hexal") && HexalWispWrapper.isWisp(context))
        || !(castHandStack.getItem() instanceof ItemGloopifact gloopifactItem)){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
            return null;
        }
        return new Pair<>(castHandStack, gloopifactItem);
    }
}
